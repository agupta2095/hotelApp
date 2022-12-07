async function liked(id, name) {
  /*var param = {"hotelId": id, "hotelName": name}
  console.log("Here in Add Favourite Hotels");
  console.log(id);
  console.log(name);
  console.log(param);
  let response = await fetch('/favHotels', {
     method :'POST',
     headers: {
         'Content-Type': 'application/json'
         "Access-Control-Origin": "*"
     },
     body: JSON.stringify(param)
    });
  console.log(response);*/
  var liked = document.getElementById("like");
  liked.innerHTML = "";
  liked.innerHTML= "<i class=\"fa fa-heart\" style=\"color:red\"></i> <span class=\"icon\">Favourite Hotel</span>";

  console.log("Here in Add Favourite Hotels");
  var xhttp = new XMLHttpRequest();
  xhttp.open("POST", "/favHotels", true);
  xhttp.setRequestHeader("Content-type",
  "application/x-www-form-urlencoded");
  xhttp.send("hotelId="+id+"&hotelName="+name);
}

var likedHotels
async function getLiked() {
   console.log("Here in Get Favourite Hotels");
   let response = await fetch('/favHotels', {method :'get'});
   let json = await response.json();

   const result = JSON.stringify(json);
   likedHotels = JSON.parse(result);
   console.log(likedHotels);
   displayFav();
}

function displayFav() {
 var showFav = document.getElementById("showFavorite");
  showFav.innerHTML="";
  for(var i = 0; i<likedHotels.length; i++) {
    showFav.innerHTML += "<a href=\"hotelInfo?hotelId=" + likedHotels[i]["hotelId"] + "\" class =\"list-group-item list-group-item-action\">" +
       likedHotels[i]["hotelName"] +"</a>";
  }
}
async function clearFav() {
   console.log("Here in Clear Favourite Hotels");
   let response = await fetch('/favHotels?clear=true', {method :'get'});
   getLiked();
}
