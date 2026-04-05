// ═══════════════════════════════════════════════════════════════
//  Election Commission of India – MongoDB Integration Layer
//  Stack: Node.js + Express + Mongoose
//  Run:   npm install express mongoose bcryptjs jsonwebtoken cors
//         node server.js
// ═══════════════════════════════════════════════════════════════

const express    = require("express");
const mongoose   = require("mongoose");
const bcrypt     = require("bcryptjs");
const jwt        = require("jsonwebtoken");
const cors       = require("cors");

const app = express();
app.use(express.json());
app.use(cors());

// ───────────────────────────────────────────────────────────────
//  Database Connection
// ───────────────────────────────────────────────────────────────
const MONGO_URI = process.env.MONGO_URI || "mongodb://127.0.0.1:27017/voting_system";
const JWT_SECRET = process.env.JWT_SECRET || "eci_secret_change_in_production";

mongoose.connect(MONGO_URI)
  .then(() => console.log("✔ Connected to MongoDB"))
  .catch(err => { console.error("✘ MongoDB connection error:", err); process.exit(1); });

// ───────────────────────────────────────────────────────────────
//  Schema: AuthUser  (login credentials)
// ───────────────────────────────────────────────────────────────
const authUserSchema = new mongoose.Schema({
  mobileNumber: {
    type: String,          // stored as string to preserve leading zeros
    required: true,
    unique: true,
    match: [/^\d{10}$/, "Mobile number must be exactly 10 digits"]
  },
  passwordHash: { type: String, required: true },
  firstName:    { type: String, required: true, trim: true },
  lastName:     { type: String, required: true, trim: true },
  createdAt:    { type: Date, default: Date.now }
});

const AuthUser = mongoose.model("AuthUser", authUserSchema);

// ───────────────────────────────────────────────────────────────
//  Schema: Voter  (full electoral roll record)
// ───────────────────────────────────────────────────────────────
const voterSchema = new mongoose.Schema({
  // Constituency
  state:       { type: String, required: true, trim: true },
  district:    { type: String, required: true, trim: true },
  assembly:    { type: String, required: true, trim: true },

  // Personal
  firstName:   { type: String, required: true, trim: true },
  lastName:    { type: String, required: true, trim: true },
  gender:      { type: String, enum: ["Male", "Female", "Other"], required: true },
  dob:         { type: String, required: true },          // "dd/mm/yyyy"
  docProof:    { type: String, required: true },
  mobileNumber:{ type: String, required: true, match: /^\d{10}$/ },
  email: {
    type: String,
    trim: true,
    lowercase: true,
    match: [/^\S+@\S+\.\S+$/, "Invalid email format"]
  },
  aadhaarNumber: {
    type: String,
    required: true,
    match: [/^\d{12}$/, "Aadhaar must be 12 digits"],
    unique: true
  },

  // Address
  apartmentNo:  { type: String, trim: true },
  area:         { type: String, trim: true },
  town:         { type: String, trim: true },
  pincode:      { type: Number, min: 100000, max: 999999 },
  taluka:       { type: String, trim: true },
  addressProof: { type: String, trim: true },

  // Family reference
  relation:     { type: String, trim: true },
  relativeName: { type: String, trim: true },
  relativeEpic: { type: Number },

  // System-generated
  epic:     { type: Number, required: true, unique: true },
  hasVoted: { type: Boolean, default: false },

  registeredAt: { type: Date, default: Date.now }
});

// Index for fast searches
voterSchema.index({ epic: 1 });
voterSchema.index({ mobileNumber: 1 });
voterSchema.index({ state: 1, district: 1 });

const Voter = mongoose.model("Voter", voterSchema);

// ───────────────────────────────────────────────────────────────
//  Schema: ElectionResult  (per-election tally)
// ───────────────────────────────────────────────────────────────
const electionSchema = new mongoose.Schema({
  electionName: { type: String, default: "General Assembly Elections" },
  parties: [
    {
      name:       String,
      shortCode:  String,
      voteCount:  { type: Number, default: 0 }
    }
  ],
  isActive:   { type: Boolean, default: true },
  startDate:  { type: Date, default: Date.now },
  endDate:    { type: Date }
});

const Election = mongoose.model("Election", electionSchema);

// ───────────────────────────────────────────────────────────────
//  Middleware: JWT authentication guard
// ───────────────────────────────────────────────────────────────
function authGuard(req, res, next) {
  const header = req.headers.authorization;
  if (!header || !header.startsWith("Bearer "))
    return res.status(401).json({ error: "Unauthorized – token missing" });

  try {
    req.user = jwt.verify(header.split(" ")[1], JWT_SECRET);
    next();
  } catch {
    res.status(401).json({ error: "Unauthorized – invalid token" });
  }
}

// ───────────────────────────────────────────────────────────────
//  Helper: generate unique 6-digit EPIC
// ───────────────────────────────────────────────────────────────
async function generateEpic() {
  let epic, exists;
  do {
    epic   = 100000 + Math.floor(Math.random() * 900000);
    exists = await Voter.exists({ epic });
  } while (exists);
  return epic;
}

// ═══════════════════════════════════════════════════════════════
//  ROUTES
// ═══════════════════════════════════════════════════════════════

// ── POST /api/auth/register ────────────────────────────────────
app.post("/api/auth/register", async (req, res) => {
  try {
    const { mobileNumber, password, firstName, lastName } = req.body;

    const pwdRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;
    if (!pwdRegex.test(password))
      return res.status(400).json({ error: "Password does not meet complexity requirements." });

    if (await AuthUser.exists({ mobileNumber }))
      return res.status(409).json({ error: "Mobile number already registered." });

    const passwordHash = await bcrypt.hash(password, 12);
    await AuthUser.create({ mobileNumber, passwordHash, firstName, lastName });

    res.status(201).json({ message: "Registration successful." });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ── POST /api/auth/login ───────────────────────────────────────
app.post("/api/auth/login", async (req, res) => {
  try {
    const { mobileNumber, password } = req.body;
    const user = await AuthUser.findOne({ mobileNumber });

    if (!user || !(await bcrypt.compare(password, user.passwordHash)))
      return res.status(401).json({ error: "Invalid credentials." });

    const token = jwt.sign(
      { id: user._id, mobile: user.mobileNumber, name: `${user.firstName} ${user.lastName}` },
      JWT_SECRET,
      { expiresIn: "8h" }
    );

    res.json({ token, name: `${user.firstName} ${user.lastName}` });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ── POST /api/voters/register ──────────────────────────────────
app.post("/api/voters/register", authGuard, async (req, res) => {
  try {
    const epic = await generateEpic();
    const voter = await Voter.create({ ...req.body, epic });
    res.status(201).json({ message: "Voter registered successfully.", epic: voter.epic });
  } catch (e) {
    if (e.code === 11000)
      return res.status(409).json({ error: "Duplicate record – Aadhaar or EPIC already exists." });
    res.status(400).json({ error: e.message });
  }
});

// ── GET /api/voters ────────────────────────────────────────────
app.get("/api/voters", authGuard, async (req, res) => {
  try {
    const { state, district, page = 1, limit = 20 } = req.query;
    const filter = {};
    if (state)    filter.state    = new RegExp(state, "i");
    if (district) filter.district = new RegExp(district, "i");

    const voters = await Voter.find(filter)
      .select("-aadhaarNumber")               // mask sensitive field
      .skip((page - 1) * limit)
      .limit(Number(limit))
      .sort({ registeredAt: -1 });

    const total = await Voter.countDocuments(filter);
    res.json({ total, page: Number(page), voters });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ── GET /api/voters/:epic ──────────────────────────────────────
app.get("/api/voters/:epic", authGuard, async (req, res) => {
  try {
    const voter = await Voter.findOne({ epic: Number(req.params.epic) })
                             .select("-aadhaarNumber");
    if (!voter) return res.status(404).json({ error: "Voter not found." });
    res.json(voter);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ── PATCH /api/voters/:epic ────────────────────────────────────
app.patch("/api/voters/:epic", authGuard, async (req, res) => {
  try {
    const allowed = ["mobileNumber", "firstName", "lastName", "email", "dob"];
    const updates = Object.fromEntries(
      Object.entries(req.body).filter(([k]) => allowed.includes(k))
    );

    const voter = await Voter.findOneAndUpdate(
      { epic: Number(req.params.epic) },
      updates,
      { new: true, runValidators: true }
    );
    if (!voter) return res.status(404).json({ error: "Voter not found." });
    res.json({ message: "Record updated successfully.", voter });
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
});

// ── DELETE /api/voters/:epic ───────────────────────────────────
app.delete("/api/voters/:epic", authGuard, async (req, res) => {
  try {
    const result = await Voter.findOneAndDelete({ epic: Number(req.params.epic) });
    if (!result) return res.status(404).json({ error: "Voter not found." });
    res.json({ message: "Voter record deleted successfully." });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ── POST /api/elections/vote ───────────────────────────────────
app.post("/api/elections/vote", authGuard, async (req, res) => {
  try {
    const { epic, partyIndex } = req.body;
    const voter    = await Voter.findOne({ epic: Number(epic) });
    if (!voter)        return res.status(404).json({ error: "Voter not found." });
    if (voter.hasVoted) return res.status(409).json({ error: "Already voted." });

    const election = await Election.findOne({ isActive: true });
    if (!election) return res.status(404).json({ error: "No active election." });
    if (partyIndex < 0 || partyIndex >= election.parties.length)
      return res.status(400).json({ error: "Invalid party selection." });

    election.parties[partyIndex].voteCount += 1;
    await election.save();

    voter.hasVoted = true;
    await voter.save();

    res.json({ message: "Vote recorded successfully. Thank you!" });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ── GET /api/elections/results ─────────────────────────────────
app.get("/api/elections/results", async (req, res) => {
  try {
    const election = await Election.findOne({ isActive: true });
    if (!election) return res.status(404).json({ error: "No active election." });

    const sorted  = [...election.parties].sort((a, b) => b.voteCount - a.voteCount);
    const total   = sorted.reduce((s, p) => s + p.voteCount, 0);
    const results = sorted.map(p => ({
      ...p.toObject(),
      percentage: total ? ((p.voteCount / total) * 100).toFixed(2) : "0.00"
    }));

    res.json({ electionName: election.electionName, totalVotes: total, results });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ── Seed default election (run once) ──────────────────────────
app.post("/api/elections/seed", async (req, res) => {
  try {
    const exists = await Election.exists({});
    if (exists) return res.status(409).json({ error: "Election already exists." });

    await Election.create({
      parties: [
        { name: "Aam Aadmi Party",              shortCode: "AAP" },
        { name: "Indian National Congress",      shortCode: "INC" },
        { name: "Bharatiya Janata Party",        shortCode: "BJP" },
        { name: "Communist Party of India (M)",  shortCode: "CPM" },
        { name: "Bahujan Samaj Party",           shortCode: "BSP" },
        { name: "National People's Party",       shortCode: "NPP" }
      ]
    });
    res.status(201).json({ message: "Election seeded." });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ───────────────────────────────────────────────────────────────
//  Start server
// ───────────────────────────────────────────────────────────────
const PORT = process.env.PORT || 5000;
app.listen(PORT, () =>
  console.log(`✔ API server running on http://localhost:${PORT}`)
);

module.exports = app; // for testing