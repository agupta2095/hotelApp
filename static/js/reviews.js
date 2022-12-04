var reviews;
function uploadReviews(hotelId) {
  console.log("Here in Upload reviews");
  let response = await fetch('/reviews?hotelId='+hotelId, {method :'get'});
  let json = await response.json();
  reviews = JSON.parse(json);
}
var currentPage = 1;
var CountPerEachPage = 5;
function numberOfPages() {
  return Math.ceil(reviews.length / CountPerEachPage);
}

function getPreviousPage() {
   if(currentPage > 1) {
      currentPage--;
      displayPage(currentPage)
   }
}

function getNextPage() {
  if(currentPage <numberOfPages()) {
    currentPage++;
    displayPage(currentPage)
  }
}

function displayPage(paginationPage) {
  var nextPage = document.getElementById("nextPage");
  var previousPage = document.getElementById("previousPage");
  var showMyTable = document.getElementById("showTable");
  var pageNumber = document.getElementById("paginationPage");
  //validating pages based on page count
  if (pageNumber < 1)
    paginationPage = 1;
  if (paginationPage > numberOfPages())
    paginationPage = numberOfPages();

  console.log(reviews.length());
  for (var i = (paginationPage - 1) * CountPerEachPage; i < (paginationPage * CountPerEachPage); i++) {
      document.write("Review by reviews[i][\"username\"] on reviews[i][\"date\"]");
      document.write("Title: reviews[i][\"title\"]");
      document.write("Rating: reviews[i][\"rating\"]");
      document.write("reviews[i][\"text\"]");
      document.write("<hr style=\"height:2px;border-width:0;color:gray;background-color:gray\">");
  }
  pageNumber.innerHTML = paginationPage;
  if (paginationPage == 1) {
    previousPage.style.visibility = "hidden";
  } else {
    previousPage.style.visibility = "visible";
  }
  if (paginationPage == numberOfPages()) {
    nextPage.style.visibility = "hidden";
  } else {
    nextPage.style.visibility = "visible";
  }
}

function myFunction() {
  displayPage(1);
};