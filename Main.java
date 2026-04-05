package PSJProject;

//package mini project;
import java.util.*;


class VotingSystem {
static ArrayList<VotingSystem> voters = new ArrayList<>();
Scanner sc = new Scanner(System.in);
public long mobile_number;
private String password;
public String voter_fName;
public String voter_lName;
//default constructor to create object
VotingSystem(){
}
//Parameterized constructor to initialize values
public VotingSystem(long mobile_number, String password, String voter_fName, String voter_lName) {
   this.mobile_number = mobile_number;
   this.password = password;
   this.voter_fName = voter_fName;
   this.voter_lName = voter_lName;
}
//method for login
public void voterLogin() {
   System.out.println("                                                                ELECTION COMMISSION OF INDIA\n");
   System.out.println("                                              If you are a registered user, enter your mobile number to login\n");
   System.out.print("Mobile Number: ");
   long inputMobile = sc.nextLong();
   sc.nextLine();
   System.out.print("Password: ");
   String inputPassword = sc.nextLine();
   boolean loginSuccess = false;
   for (VotingSystem voter : voters) {
      if (voter.mobile_number == inputMobile && voter.password.equals(inputPassword)) {
           System.out.println("Login successful! Welcome, " + voter.voter_fName + " " + voter.voter_lName);
           Main.homePage();
           loginSuccess = true;
           break;
       }
       }
   if (!loginSuccess) {
       System.out.println("Incorrect mobile number or password. Please try again.");
   }
}
//method for registration


private boolean isValidPassword(String password) {
    // At least 8 characters, one uppercase, one digit, and one special character
    return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");
}


public void voterRegistration() {
        try {
            System.out.println("                                                                        VOTER REGISTRATION\n");
            System.out.println("                                                                Please enter the required fields\n");
           
            System.out.print("Mobile Number: ");
            Long mobileInput = sc.nextLong();


            sc.nextLine(); // Clear newline from buffer


             if (mobileInput < 1000000000L || mobileInput > 9999999999L) {
             throw new IllegalArgumentException("Mobile number must be exactly 10 digits.");
            }


            this.mobile_number = mobileInput;


   
            System.out.println("The password must be 8 characters long and must include at least one capital letter, number, and special symbol.");
            System.out.print("Set Password: ");
            this.password = sc.nextLine();
   
            if (!isValidPassword(this.password)) {
                throw new IllegalArgumentException("Password must be at least 8 characters and include one uppercase letter, one number, and one special character.");
            }
   
            System.out.print("Confirm Password: ");
            String confirmPassword = sc.nextLine();
            if (!this.password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
   
            System.out.print("First Name: ");
            this.voter_fName = sc.nextLine();
   
            System.out.print("Last Name: ");
            this.voter_lName = sc.nextLine();
   
            // create and add voter
            VotingSystem newVoter = new VotingSystem(mobile_number, password, voter_fName, voter_lName);
            voters.add(newVoter);
            System.out.println("\nRegistration successful! You can now log in.");
   
            this.voterLogin();
   
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred.");
        }    
}
}


class Voter{
long mobileNumber;
String password;
String voterFirstName;
String voterLastName;
String state;
String district;
String assembly;
String dob;
String doc;
String gender;
long aadhaarNumber;
String email;
String apartmentNo;
String area;
String town;
int pincode;
String taluka;
String addressProof;
String relation;
String relative_name;
int relative_id;
int epic;
boolean hasVoted;
Voter(){
}
Voter(long mobileNumber,String voterFirstName, String voterLastName, String state,String district, String assembly, String dob, String doc, String gender, long aadhaarNumber,String email,String apartmentNo,String area,String town,int pincode,String taluka,String addressProof, String relation, String relative_name, int relative_id, int epic){
this.mobileNumber = mobileNumber;
this.voterFirstName = voterFirstName;
this.voterLastName = voterLastName;
this.state = state;
this.district= district;
this.assembly= assembly;
this.dob = dob;
this.doc = doc;
this.gender = gender;
this.email = email;
this.aadhaarNumber = aadhaarNumber;
this.apartmentNo =apartmentNo;
this.area = area;
this.town = town;
this.pincode = pincode;
this.taluka = taluka;
this.addressProof = addressProof;
this.relation= relation;
this.relative_name= relative_name;
this.relative_id = relative_id;
this.epic = epic;
this.hasVoted = false;
}
//ArrayList for voter registration number details
static ArrayList<Voter> voters = new ArrayList<>();
Scanner sc = new Scanner(System.in);
public void display() {
   System.out.println("Name: " + voterFirstName + " " + voterLastName);
   System.out.println("State: " + state);
   System.out.println("District: " + district);
   System.out.println("Date of Birth: " + dob);
   System.out.println("Gender: " + gender);
   System.out.println("Aadhaar Number: " + aadhaarNumber);
   System.out.println("Email Address: " + email);
   System.out.println("Pincode: " + pincode);
   System.out.println("EPIC Number: " + epic);
   System.out.println("-----------------------------");
}


public Voter findVoterByEpic(int epicNumber) {
    for (Voter voter : voters) {
        if (voter.epic == epicNumber) {
            return voter;
        }
    }
    return null;
}


void voterIdentity(){
System.out.println("                                                 EVERY VOTE COUNTS- YOUR VOTER CARD IS YOUR IDENTITY\n");
System.out.println("                                                     Forms for Registration in Electoral Roll\n");
System.out.println(
   "FORM 1: NEW VOTER REGISTRATION\fill form 1 if you are 18 years if above or turning 18 in few months\n" + //
   "FORM 2: DELETION\fill form 2 to get name deleted from the existing Electoral Roll\n" +
   "FORM 3: CORRECTION OF ENTRIES\fill form 3 for Correction\n");
System.out.print("Which form do you wish to fill?: ");
int form_number = sc.nextInt();
Voter form = new Voter();
switch (form_number){
case 1:
form.form();
break;
case 2:
form.deleteRecord();
break;
case 3:
form.correctDetails();
Main.homePage();
break;
}
}


void form(){
    System.out.println("\n--- Form 1: New Voter Registration in Electoral Roll --- \n");
    System.out.println("Every Indian citizen who has attained the age of 18 years on the qualifying date i.e. first day of January of the year of revision of electoral roll, otherwise disqualified.\n");
    System.out.println("Enter Voting Details\n");
    System.out.print("State/UT: ");
    state = sc.nextLine();
    System.out.print("District: ");
    district = sc.nextLine();
    System.out.print("Assembly Constituency: ");
    assembly = sc.nextLine();
    System.out.print("Date of Birth(dd/mm/yyyy): ");
    dob = sc.nextLine();
    System.out.print("Document proof of Date of Birth(Aadhaar Card/Birth Certificate/Domicile or Nationality Certificate): ");
    doc = sc.nextLine();
   
    System.out.print("Save changes and move to Personal Details? (Press 1): ");
    int c = sc.nextInt();
    sc.nextLine();


    System.out.print("\nFirst name of the voter: ");
    voterFirstName = sc.nextLine();
    System.out.print("Last name of the voter: ");
    voterLastName = sc.nextLine();
    System.out.print("Gender of the voter(Female/Male/Other): ");
    gender = sc.nextLine();
    System.out.print("Enter registered mobile number: ");
    mobileNumber = sc.nextLong();
   
    sc.nextLine();
    System.out.print("Enter email id: ");
    email = sc.nextLine();
    System.out.print("Aadhaar number of the voter: ");
    aadhaarNumber = sc.nextLong();
    sc.nextLine();
    System.out.print("Save changes and move to Residential Details? (Press 1): ");
    int c1 = sc.nextInt();
    sc.nextLine();
    System.out.print("\nHouse/Building/Apartment No.: ");
    apartmentNo = sc.nextLine();
    System.out.print("Street/Area/Road/Locality: ");
    area = sc.nextLine();
    System.out.print("Town/Village: ");
    town = sc.nextLine();
    System.out.print("Pincode: ");
    pincode = sc.nextInt();
    sc.nextLine();
    System.out.print("Tehsil/Taluka/Mandal: ");
    taluka = sc.nextLine();
    System.out.print("Address Proof of voter(Electricity bill/ Rent Lease/ Water Bill/ Rent Agreement): ");
    addressProof = sc.nextLine();
    System.out.print("Save changes and move to Family Details? (Press 1): ");
    int c2 = sc.nextInt();
    sc.nextLine();
    System.out.print("\nRelation with the voter(Mother/ Father/ Sibling): ");
    relation = sc.nextLine();
    System.out.print("Name of the Member: ");
    relative_name = sc.nextLine();
    System.out.print("Enter Voter ID number of the family member: \n");
    relative_id = sc.nextInt();
   
    sc.nextLine();
    System.out.println("I hereby declare that to the best of my knowledge and belief all the details I have entered are correct\n");
    Random random = new Random();
    this.epic = 100000 + random.nextInt(900000);
    System.out.println("Your application has been submitted successfully and generated Electoral Photo Identity Card number is "+ epic +" You can use this number for any future reference. \n" );
    Voter newVoter1 = new Voter(mobileNumber,voterFirstName,voterLastName,state,district,assembly,dob,doc,gender,aadhaarNumber,email,apartmentNo,area,town,pincode,taluka,addressProof,relation,relative_name,relative_id,epic);
    voters.add(newVoter1);
   
    Main.homePage();
    }


void deleteRecord() {
        System.out.println("\n--- Form 2: Deletion from Electoral Roll ---");
 
        System.out.print("Enter your EPIC: ");
        int inputRef = sc.nextInt();
 
        boolean found = false;
 
        for (int i = 0; i < voters.size(); i++) {
            if (voters.get(i).epic == inputRef) {
                voters.remove(i);
                System.out.println("Voter record with EPIC " + inputRef + " has been successfully deleted.");
                Main.homePage();
                found = true;
                break;
            }
        }
 
        if (!found) {
            System.out.println("No record found with EPIC " + inputRef);
            Main.homePage();
        }
    }  
   
void correctDetails() {
        System.out.print("Enter your EPIC number: ");
        int inputref = sc.nextInt();
   
        Voter matchedVoter = findVoterByEpic(inputref);
        if (matchedVoter == null) {
            System.out.println("No voter found with the given EPIC number.");
            return;
        }
   
        System.out.println("Which field do you wish to Correct?\n" +
            "1. Mobile Number\n" +
            "2. Voter First Name\n" +
            "3. Voter Last Name\n" +
            "4. Email Address\n" +
            "5. Date of Birth");
        int choice = sc.nextInt();
        sc.nextLine();
   
        switch (choice) {
            case 1:
                System.out.print("Enter new mobile number: ");
                long inputMobile = sc.nextLong();
                matchedVoter.mobileNumber = inputMobile;
                break;
            case 2:
                System.out.print("Enter new first name: ");
                String firstName = sc.nextLine();
                matchedVoter.voterFirstName = firstName;
                break;
            case 3:
                System.out.print("Enter new last name: ");
                String lastName = sc.nextLine();
                matchedVoter.voterLastName = lastName;
                break;
            case 4:
                System.out.print("Enter new email: ");
                String inpEmail = sc.nextLine();
                matchedVoter.email = inpEmail;
                break;
            case 5:
                System.out.print("Enter Date of Birth: ");
                String d = sc.nextLine();
                matchedVoter.dob = d;
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
   
        System.out.println("Voter updated successfully!");
    }    
}


class DisplayDetails extends Voter{
   public void display() {
       System.out.println("\nHere are the Voter details:\n ");
       for (Voter voter : voters) {
           voter.display();
       }
   }
}


class CastVote extends Voter {
   Scanner sc = new Scanner(System.in);
   static List<String> candidates = Arrays.asList(
    "AAP (Aam Aadmi Party)", " INC (Indian National Congress) ", " BJP (Bharatiya Janata Party)", " CPM (Communist Party of India)", " BSP (Bahujan Samaj Party)"," NPP (National People's Party)" );
    static int[] voteCounts = new int[6];




   public void display() {
       boolean hasVoted;
       System.out.println("---------------------------------------------------------List of Political Parties-------------------------------------\n");
       System.out.println("                                                    General Elections to Assembly Constituencies");
       System.out.println("1. AAP (Aam Aadmi Party) \n2. INC (Indian National Congress) \n3. BJP (Bharatiya Janata Party) \n4. CPM (Communist Party of India) \n5. BSP (Bahujan Samaj Party) \n6. NPP (National People's Party)");
       
       System.out.print("Enter your EPIC Number: ");
       int inputRef = sc.nextInt();
       boolean found = false;
       for (Voter voter : voters) {
           if (voter.epic == inputRef) {
               found = true;
               if (voter.hasVoted) {
                   System.out.println("You can't vote again.");
                   return;
               }
               System.out.print("CAST YOUR VOTE: ");
               int choice = sc.nextInt();
               if (choice >= 1 && choice <= 6) {
                   voteCounts[choice - 1] += 1;
                   voter.hasVoted = true;
                   System.out.println("Your vote has been recorded successfully\nTHANK YOU FOR VOTING");
               } else {
                   System.out.println("Invalid choice.");
               }
               return;
           }
       }
       if (!found) {
           System.out.println("EPIC not found.");
       }
   }
      void delay(int milliseconds) {
       long startTime = System.currentTimeMillis();
       while (System.currentTimeMillis() - startTime < milliseconds){  
       }
    }


   void viewResults() {                      
       System.out.println("\n                                             General Elections to Assembly Constituencies");
                      delay(1000);
       System.out.println("Compiling Results");
                       delay(1000);
       System.out.println("This might take a second");
                      delay(1000);
       System.out.println("Here are the votes casted to the parties: ");
       boolean anyVotes = false;
                    int max = -1, winner = -1;
                    for (int i = 0; i < voteCounts.length; i++) {
                        if (voteCounts[i] > 0) {
                            anyVotes = true;
                        }
                        System.out.println(candidates.get(i) + ": " + voteCounts[i] + " votes");
                                       delay(1000);
                        if (voteCounts[i] > max) {
                            max = voteCounts[i];
                            winner = i;
                        }
                    }
                    if (!anyVotes) {
                        System.out.println("No votes have been cast yet.");
                    } else {
                        System.out.println("So the winning party is: ");
                        delay(1000);
                        System.out.println(candidates.get(winner) + " with " + max + " votes.");
                    }


   }
}


class SearchVoter extends Voter{
    void search(){
        System.out.print("Enter your EPIC number: ");
        int epicSearch = sc.nextInt();
        boolean found = false;
        for (Voter voter : voters) {
            if (voter.epic == epicSearch) {
                System.out.println("Voter found: " + voter.voterFirstName + " " + voter.voterLastName);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No record found with the given EPIC number.");
        }
    }
}


class Main{


static Scanner sc = new Scanner(System.in);
public static void main(String[] args) {
   VotingSystem vha = new VotingSystem();
   System.out.println("                                                                   ELECTION COMMISSION OF INDIA");
   System.out.println("                                                                   Register to voice your vote!");
 
   vha.voterRegistration();
}
public static void homePage() {


DisplayDetails add = new DisplayDetails();
Voter v = new Voter();
CastVote cv = new CastVote();
SearchVoter sv = new SearchVoter();
   System.out.println("                                                        **WELCOME TO THE ELECTION COMMISION OF INDIA VOTER HELPLINE**");
   System.out.println("What can we assist you with today?");
   System.out.println(
               "1. Voter Registration\n" +
               "2. Display voter details \n" +
               "3. Search your name in Electoral Roll\n" +
               "4. Ongoing Elections - Cast your vote \n" +
               "5. View Election results\n"+
               "6. Logout");
   System.out.print("Enter your choice: ");
   int cop = sc.nextInt();
   switch (cop){
       case 1:
          v.voterIdentity();
          break;
       case 2:
          add.display();
          Main.homePage();
         
          break;
       case 3:
           sv.search();
           Main.homePage();
          break;
       case 4:
          cv.display();
          Main.homePage();
          break;
     
       case 5:
          cv.viewResults();
          Main.homePage();
          break;
       case 6:
         System.out.println("Logging you out");
          break;
    default:
    System.out.println("Invalid Choice");
    Main.homePage();
    break;
}
}
}
