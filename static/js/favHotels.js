async function liked(hotelId) {
  console.log("Here in Add Favourite Hotels");
  let response = await fetch('/favHotels?hotelId='+hotelId, {method :'get'});
  console.log(response);
}
var likedHotels
async function getLiked() {
   console.log("Here in Add Favourite Hotels");
   let response = await fetch('/favHotels', {method :'get'});
   let json = await response.json();
   console.log(json);
   const result = JSON.stringify(json);
   likedHotels = JSON.parse(result);
}

