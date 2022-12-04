
     var reviews;
     async function uploadReviews(hotelId) {
       console.log("Here in Upload reviews");
       let response = await fetch('/reviews?hotelId='+hotelId, {method :'get'});
       let json = await response.json();
       console.log(json);
       const result = JSON.stringify(json);
       reviews = JSON.parse(result);
       console.log(reviews.length);
       displayPage(1);
     }
     var currentPage = 1;
     var CountPerEachPage = 5;
     function numberOfPages() {
        return Math.ceil(reviews.length / CountPerEachPage);
     }

     function getPreviousPage() {
       if(currentPage > 1) {
         currentPage--;
         displayPage(currentPage);
       }
     }

     function getNextPage() {
       if(currentPage <numberOfPages()) {
         currentPage++;
         displayPage(currentPage);
       }
     }

     function displayPage(paginationPage) {
       var nextPage = document.getElementById("nextPage");
       var previousPage = document.getElementById("previousPage");
       var showMyReviews = document.getElementById("showReviews");
       var pageNumber = document.getElementById("paginationPage");
       //validating pages based on page count
       if (pageNumber < 1)
       paginationPage = 1;
       if (paginationPage > numberOfPages())
       paginationPage = numberOfPages();

       showMyReviews.innerHTML = "";
       for (var i = (paginationPage - 1) * CountPerEachPage; i < (paginationPage * CountPerEachPage); i++) {
         showMyReviews.innerHTML += "Review by " + reviews[i]["username"] + " on " + reviews[i]["date"] + "<br>";
         showMyReviews.innerHTML += "Title: " + reviews[i]["title"] + "<br>";
         showMyReviews.innerHTML +="Rating: " + reviews[i]["rating"] +"<br>";
         showMyReviews.innerHTML += reviews[i]["text"];
         showMyReviews.innerHTML += "<hr style=\"height:2px;border-width:0;color:gray;background-color:gray\">";
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
