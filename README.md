# E-Voting-System
Full-stack voter management portal simulating ECI's digital infrastructure — Java console app + Node/Express REST API + MongoDB + HTML/CSS/JS frontend.

# Election Commission of India – Voter Helpline Portal

A full-stack voter management system simulating the Election Commission of India's
digital infrastructure. Built as a mini project for managing electoral rolls,
voter registration, and conducting general elections.

## Features
- Voter registration with multi-step form (Form 1 / 2 / 3)
- Login and registration with password validation
- Search, update, and delete voter records by EPIC number
- Cast votes in active elections (one vote per EPIC enforced)
- Live election results with party-wise tally and winner declaration

## Tech Stack
- Java (console application with OOP structure)
- HTML / CSS / JavaScript (frontend portal)
- Node.js + Express (REST API backend)
- MongoDB + Mongoose (database)
- JWT authentication + bcryptjs password hashing

## Project Structure
PSJProject/
├── VotingSystem.java   # Java console app
├── index.html          # Frontend voter portal
├── server.js           # Node.js + Express REST API
└── package.json        # Node dependencies

## How to Run
1. Start MongoDB:        mongod
2. Install deps:         npm install
3. Start server:         node server.js
4. Open frontend:        open index.html in browser
5. Seed election data:   POST http://localhost:5000/api/elections/seed

## Authors
Built as part of a Java mini project with full-stack extension.
