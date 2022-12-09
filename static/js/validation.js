function myValidation(error) {
  console.log("Here in Validation form");
  if(error == 4) {
     alert("Incorrect Username or password");
  } else if(error == 1) {
     alert("Username already taken!");
  } else if(error == 2) {
     alert("Passwords don't match!");
  } else if(error == 3) {
     alert("Password must contain at-least one digit, alphabet and special character and minimum 6 characters.")
  }
}