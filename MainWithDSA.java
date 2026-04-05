package PSJProject;

import java.util.*;

// ─────────────────────────────────────────────────────────────
//  Model: AuthUser  –  holds login credentials only
// ─────────────────────────────────────────────────────────────
class AuthUser {
    private final long   mobileNumber;
    private final String password;
    private final String firstName;
    private final String lastName;

    public AuthUser(long mobileNumber, String password,
                    String firstName, String lastName) {
        this.mobileNumber = mobileNumber;
        this.password      = password;
        this.firstName     = firstName;
        this.lastName      = lastName;
    }

    public long   getMobileNumber() { return mobileNumber; }
    public boolean authenticate(long mobile, String pwd) {
        return this.mobileNumber == mobile && this.password.equals(pwd);
    }
    public String getFullName() { return firstName + " " + lastName; }
}

// ─────────────────────────────────────────────────────────────
//  Model: Voter  –  full electoral-roll record
// ─────────────────────────────────────────────────────────────
class Voter {
    // Personal
    long   mobileNumber;
    String firstName, lastName, gender, email, dob, docProof;
    long   aadhaarNumber;

    // Constituency
    String state, district, assembly;

    // Address
    String apartmentNo, area, town, taluka, addressProof;
    int    pincode;

    // Family
    String relation, relativeName;
    int    relativeId;

    // System-generated
    int     epic;
    boolean hasVoted;

    Voter() {}

    Voter(long mobileNumber, String firstName, String lastName,
          String state, String district, String assembly,
          String dob, String docProof, String gender,
          long aadhaarNumber, String email,
          String apartmentNo, String area, String town,
          int pincode, String taluka, String addressProof,
          String relation, String relativeName, int relativeId, int epic) {

        this.mobileNumber  = mobileNumber;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.state         = state;
        this.district      = district;
        this.assembly      = assembly;
        this.dob           = dob;
        this.docProof      = docProof;
        this.gender        = gender;
        this.aadhaarNumber = aadhaarNumber;
        this.email         = email;
        this.apartmentNo   = apartmentNo;
        this.area          = area;
        this.town          = town;
        this.pincode       = pincode;
        this.taluka        = taluka;
        this.addressProof  = addressProof;
        this.relation      = relation;
        this.relativeName  = relativeName;
        this.relativeId    = relativeId;
        this.epic          = epic;
        this.hasVoted      = false;
    }

    public void printSummary() {
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf ("│  %-40s│%n", "VOTER RECORD");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.printf ("│  Name       : %-26s│%n", firstName + " " + lastName);
        System.out.printf ("│  EPIC       : %-26d│%n", epic);
        System.out.printf ("│  State      : %-26s│%n", state);
        System.out.printf ("│  District   : %-26s│%n", district);
        System.out.printf ("│  DOB        : %-26s│%n", dob);
        System.out.printf ("│  Gender     : %-26s│%n", gender);
        System.out.printf ("│  Aadhaar    : %-26d│%n", aadhaarNumber);
        System.out.printf ("│  Email      : %-26s│%n", email);
        System.out.printf ("│  Pincode    : %-26d│%n", pincode);
        System.out.println("└─────────────────────────────────────────┘");
    }
}

// ─────────────────────────────────────────────────────────────
//  Repository: in-memory stores (replace with MongoDB layer)
// ─────────────────────────────────────────────────────────────
class Repository {
    static final List<AuthUser> authUsers = new ArrayList<>();
    static final List<Voter>    voters    = new ArrayList<>();

    static Optional<AuthUser> findAuthUser(long mobile, String pwd) {
        return authUsers.stream()
                        .filter(u -> u.authenticate(mobile, pwd))
                        .findFirst();
    }

    static Optional<Voter> findVoterByEpic(int epic) {
        return voters.stream()
                     .filter(v -> v.epic == epic)
                     .findFirst();
    }

    static boolean epicExists(int epic) {
        return voters.stream().anyMatch(v -> v.epic == epic);
    }

    static boolean mobileRegistered(long mobile) {
        return authUsers.stream().anyMatch(u -> u.getMobileNumber() == mobile);
    }
}

// ─────────────────────────────────────────────────────────────
//  Service: AuthService
// ─────────────────────────────────────────────────────────────
class AuthService {
    private final Scanner sc = new Scanner(System.in);

    private boolean isValidPassword(String pwd) {
        return pwd.matches(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$"
        );
    }

    private boolean isValidMobile(long mobile) {
        return mobile >= 1_000_000_000L && mobile <= 9_999_999_999L;
    }

    /** Returns true on successful registration + login. */
    public boolean register() {
        printHeader("VOTER REGISTRATION");
        try {
            System.out.print("Mobile Number  : ");
            long mobile = sc.nextLong(); sc.nextLine();

            if (!isValidMobile(mobile))
                throw new IllegalArgumentException("Mobile number must be exactly 10 digits.");
            if (Repository.mobileRegistered(mobile))
                throw new IllegalArgumentException("This mobile number is already registered.");

            System.out.println("Password rules : Min 8 chars | 1 uppercase | 1 digit | 1 special char");
            System.out.print("Set Password   : ");
            String pwd = sc.nextLine();
            if (!isValidPassword(pwd))
                throw new IllegalArgumentException(
                    "Password does not meet the required criteria.");

            System.out.print("Confirm Pwd    : ");
            if (!pwd.equals(sc.nextLine()))
                throw new IllegalArgumentException("Passwords do not match.");

            System.out.print("First Name     : ");
            String fn = sc.nextLine().trim();
            System.out.print("Last Name      : ");
            String ln = sc.nextLine().trim();

            Repository.authUsers.add(new AuthUser(mobile, pwd, fn, ln));
            System.out.println("\n✔ Registration successful! Proceeding to login…\n");
            return login();

        } catch (IllegalArgumentException e) {
            System.out.println("✘ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✘ An unexpected error occurred during registration.");
        }
        return false;
    }

    /** Returns true on successful login. */
    public boolean login() {
        printHeader("VOTER LOGIN");
        System.out.print("Mobile Number : ");
        long mobile = sc.nextLong(); sc.nextLine();
        System.out.print("Password      : ");
        String pwd = sc.nextLine();

        Optional<AuthUser> user = Repository.findAuthUser(mobile, pwd);
        if (user.isPresent()) {
            System.out.println("\n✔ Welcome, " + user.get().getFullName() + "!\n");
            return true;
        }
        System.out.println("✘ Invalid credentials. Please try again.");
        return false;
    }

    private static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf ("║  %-40s║%n", title);
        System.out.println("╚══════════════════════════════════════════╝\n");
    }
}

// ─────────────────────────────────────────────────────────────
//  Service: VoterService  (forms 1 / 2 / 3)
// ─────────────────────────────────────────────────────────────
class VoterService {
    private final Scanner sc = new Scanner(System.in);

    public void showMenu() {
        printHeader("ELECTORAL ROLL MANAGEMENT");
        System.out.println("  1. Form 1 – New Voter Registration");
        System.out.println("  2. Form 2 – Deletion from Electoral Roll");
        System.out.println("  3. Form 3 – Correction of Entries");
        System.out.print("\nSelect form: ");
        int form = sc.nextInt(); sc.nextLine();

        switch (form) {
            case 1 -> registerVoter();
            case 2 -> deleteVoter();
            case 3 -> correctVoter();
            default -> System.out.println("✘ Invalid selection.");
        }
    }

    private void registerVoter() {
        printHeader("FORM 1 – NEW VOTER REGISTRATION");
        System.out.println("Eligibility: Indian citizen aged 18+ as on 1st January of the revision year.\n");

        // ── Constituency ──────────────────────────────────────
        System.out.print("State / UT                   : "); String state    = sc.nextLine();
        System.out.print("District                     : "); String district  = sc.nextLine();
        System.out.print("Assembly Constituency        : "); String assembly  = sc.nextLine();
        System.out.print("Date of Birth (dd/mm/yyyy)   : "); String dob       = sc.nextLine();
        System.out.print("DOB Proof Document           : "); String docProof  = sc.nextLine();
        prompt("Save & continue to Personal Details");

        // ── Personal ──────────────────────────────────────────
        System.out.print("First Name                   : "); String fn        = sc.nextLine();
        System.out.print("Last Name                    : "); String ln        = sc.nextLine();
        System.out.print("Gender (Female/Male/Other)   : "); String gender    = sc.nextLine();
        System.out.print("Registered Mobile Number     : "); long mobile      = sc.nextLong(); sc.nextLine();
        System.out.print("Email ID                     : "); String email     = sc.nextLine();
        System.out.print("Aadhaar Number               : "); long aadhaar     = sc.nextLong(); sc.nextLine();
        prompt("Save & continue to Residential Details");

        // ── Address ───────────────────────────────────────────
        System.out.print("House / Building / Apt No.   : "); String apt       = sc.nextLine();
        System.out.print("Street / Area / Locality     : "); String area      = sc.nextLine();
        System.out.print("Town / Village               : "); String town      = sc.nextLine();
        System.out.print("Pincode                      : "); int pincode      = sc.nextInt(); sc.nextLine();
        System.out.print("Tehsil / Taluka / Mandal     : "); String taluka    = sc.nextLine();
        System.out.print("Address Proof Document       : "); String addrProof = sc.nextLine();
        prompt("Save & continue to Family Details");

        // ── Family ────────────────────────────────────────────
        System.out.print("Relation (Mother/Father/Sibling): "); String relation = sc.nextLine();
        System.out.print("Family Member's Name         : "); String relName  = sc.nextLine();
        System.out.print("Family Member's EPIC No.     : "); int relId       = sc.nextInt(); sc.nextLine();

        // Generate unique EPIC
        int epic = generateUniqueEpic();

        Voter v = new Voter(mobile, fn, ln, state, district, assembly,
                            dob, docProof, gender, aadhaar, email,
                            apt, area, town, pincode, taluka, addrProof,
                            relation, relName, relId, epic);
        Repository.voters.add(v);

        System.out.println("\n✔ Application submitted successfully.");
        System.out.println("  Your EPIC Number is: " + epic);
        System.out.println("  Please note this number for future reference.\n");
    }

    private void deleteVoter() {
        printHeader("FORM 2 – DELETION FROM ELECTORAL ROLL");
        System.out.print("Enter your EPIC Number: ");
        int epic = sc.nextInt(); sc.nextLine();

        Optional<Voter> found = Repository.findVoterByEpic(epic);
        if (found.isPresent()) {
            Repository.voters.remove(found.get());
            System.out.println("✔ Voter record with EPIC " + epic + " deleted successfully.");
        } else {
            System.out.println("✘ No record found for EPIC " + epic + ".");
        }
    }

    private void correctVoter() {
        printHeader("FORM 3 – CORRECTION OF ENTRIES");
        System.out.print("Enter your EPIC Number: ");
        int epic = sc.nextInt(); sc.nextLine();

        Optional<Voter> opt = Repository.findVoterByEpic(epic);
        if (opt.isEmpty()) { System.out.println("✘ Voter not found."); return; }
        Voter v = opt.get();

        System.out.println("\nSelect field to correct:");
        System.out.println("  1. Mobile Number");
        System.out.println("  2. First Name");
        System.out.println("  3. Last Name");
        System.out.println("  4. Email Address");
        System.out.println("  5. Date of Birth");
        System.out.print("Choice: ");
        int choice = sc.nextInt(); sc.nextLine();

        switch (choice) {
            case 1 -> { System.out.print("New Mobile Number : "); v.mobileNumber = sc.nextLong(); sc.nextLine(); }
            case 2 -> { System.out.print("New First Name    : "); v.firstName    = sc.nextLine(); }
            case 3 -> { System.out.print("New Last Name     : "); v.lastName     = sc.nextLine(); }
            case 4 -> { System.out.print("New Email Address : "); v.email        = sc.nextLine(); }
            case 5 -> { System.out.print("New Date of Birth : "); v.dob          = sc.nextLine(); }
            default -> { System.out.println("✘ Invalid choice."); return; }
        }
        System.out.println("✔ Record updated successfully.");
    }

    private int generateUniqueEpic() {
        Random rng = new Random();
        int epic;
        do { epic = 100_000 + rng.nextInt(900_000); }
        while (Repository.epicExists(epic));
        return epic;
    }

    private void prompt(String msg) {
        System.out.print(msg + " (press Enter): ");
        sc.nextLine();
    }

    private static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf ("║  %-40s║%n", title);
        System.out.println("╚══════════════════════════════════════════╝\n");
    }
}

// ─────────────────────────────────────────────────────────────
//  Service: ElectionService  (cast vote + view results)
// ─────────────────────────────────────────────────────────────
class ElectionService {
    private static final List<String> PARTIES = List.of(
        "AAP – Aam Aadmi Party",
        "INC – Indian National Congress",
        "BJP – Bharatiya Janata Party",
        "CPM – Communist Party of India (Marxist)",
        "BSP – Bahujan Samaj Party",
        "NPP – National People's Party"
    );

    private static final int[] VOTE_COUNTS = new int[PARTIES.size()];
    private final Scanner sc = new Scanner(System.in);

    public void castVote() {
        printHeader("CAST YOUR VOTE");
        System.out.println("General Elections – Assembly Constituencies\n");

        for (int i = 0; i < PARTIES.size(); i++)
            System.out.printf("  %d. %s%n", i + 1, PARTIES.get(i));

        System.out.print("\nEnter your EPIC Number: ");
        int epic = sc.nextInt(); sc.nextLine();

        Optional<Voter> opt = Repository.findVoterByEpic(epic);
        if (opt.isEmpty()) { System.out.println("✘ EPIC not found."); return; }

        Voter voter = opt.get();
        if (voter.hasVoted) { System.out.println("✘ You have already cast your vote."); return; }

        System.out.print("\nEnter party number to vote for: ");
        int choice = sc.nextInt(); sc.nextLine();

        if (choice < 1 || choice > PARTIES.size()) {
            System.out.println("✘ Invalid choice."); return;
        }

        VOTE_COUNTS[choice - 1]++;
        voter.hasVoted = true;
        System.out.println("\n✔ Your vote has been recorded. Thank you for voting!");
    }

    public void viewResults() {
        printHeader("ELECTION RESULTS");
        System.out.println("General Elections – Assembly Constituencies\n");
        delay(800);

        int max = -1, winnerIdx = -1;
        boolean anyVotes = false;

        for (int i = 0; i < PARTIES.size(); i++) {
            System.out.printf("  %-45s : %d vote(s)%n", PARTIES.get(i), VOTE_COUNTS[i]);
            delay(600);
            if (VOTE_COUNTS[i] > 0) anyVotes = true;
            if (VOTE_COUNTS[i] > max) { max = VOTE_COUNTS[i]; winnerIdx = i; }
        }

        System.out.println();
        if (!anyVotes) {
            System.out.println("  No votes have been cast yet.");
        } else {
            delay(1000);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf ("  🏆 WINNER: %s with %d vote(s)%n",
                               PARTIES.get(winnerIdx), max);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    private void delay(int ms) {
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < ms) {}
    }

    private static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf ("║  %-40s║%n", title);
        System.out.println("╚══════════════════════════════════════════╝\n");
    }
}

// ─────────────────────────────────────────────────────────────
//  Service: SearchService
// ─────────────────────────────────────────────────────────────
class SearchService {
    private final Scanner sc = new Scanner(System.in);

    public void searchByEpic() {
        System.out.print("Enter EPIC Number to search: ");
        int epic = sc.nextInt(); sc.nextLine();

        Repository.findVoterByEpic(epic).ifPresentOrElse(
            v -> {
                System.out.println("✔ Voter found:");
                v.printSummary();
            },
            () -> System.out.println("✘ No record found for EPIC " + epic + ".")
        );
    }
}

// ─────────────────────────────────────────────────────────────
//  Service: DisplayService
// ─────────────────────────────────────────────────────────────
class DisplayService {
    public void displayAll() {
        if (Repository.voters.isEmpty()) {
            System.out.println("  No voter records registered yet.");
            return;
        }
        System.out.println("\n  Total registered voters: " + Repository.voters.size() + "\n");
        for (Voter v : Repository.voters) v.printSummary();
    }
}

// ─────────────────────────────────────────────────────────────
//  Application Entry Point
// ─────────────────────────────────────────────────────────────
class MainWithDSA {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        AuthService authService = new AuthService();

        System.out.println("  1. Register & Login");
        System.out.println("  2. Login (existing user)");
        System.out.print("\nChoice: ");
        int option = sc.nextInt(); sc.nextLine();

        boolean authenticated = switch (option) {
            case 1 -> authService.register();
            case 2 -> authService.login();
            default -> { System.out.println("Invalid option."); yield false; }
        };

        if (authenticated) homePage();
    }

    public static void homePage() {
        VoterService   voterSvc   = new VoterService();
        ElectionService elecSvc   = new ElectionService();
        SearchService  searchSvc  = new SearchService();
        DisplayService displaySvc = new DisplayService();

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║   ELECTION COMMISSION OF INDIA – PORTAL  ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1. Voter Registration (Electoral Roll)  ║");
            System.out.println("║  2. Display All Voter Details            ║");
            System.out.println("║  3. Search Voter by EPIC                 ║");
            System.out.println("║  4. Cast Your Vote                       ║");
            System.out.println("║  5. View Election Results                ║");
            System.out.println("║  6. Logout                               ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt(); sc.nextLine();
            System.out.println();

            switch (choice) {
                case 1 -> voterSvc.showMenu();
                case 2 -> displaySvc.displayAll();
                case 3 -> searchSvc.searchByEpic();
                case 4 -> elecSvc.castVote();
                case 5 -> elecSvc.viewResults();
                case 6 -> { System.out.println("  Logging out. Goodbye!\n"); return; }
                default -> System.out.println("✘ Invalid choice. Please try again.");
            }
        }
    }

    private static void printBanner() {
        System.out.println("""
            ╔═══════════════════════════════════════════════════════╗
            ║         ELECTION COMMISSION OF INDIA                  ║
            ║         Voter Helpline & Management Portal            ║
            ║                                                       ║
            ║         "Every Vote Counts – Your Voice Matters"      ║
            ╚═══════════════════════════════════════════════════════╝
            """);
    }
}