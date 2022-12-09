
     var reviews;
     async function uploadReviews(hotelId) {
       console.log("Here in Upload reviews");
       let response = await fetch('/reviews?hotelId='+hotelId, {method :'get'});
       let json = await response.json();
       console.log(json);
       const result = JSON.stringify(json);
       reviews = JSON.parse(result);
       console.log(reviews.length);
       displayRating();
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
       if(currentPage <=numberOfPages()) {
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
       for (var i = (paginationPage - 1) * CountPerEachPage; i < (paginationPage * CountPerEachPage) && i< reviews.length; i++) {
         showMyReviews.innerHTML +="<h5 style =\"color:black\">" + reviews[i]["rating"] +"/5</h5>";
         showMyReviews.innerHTML += "<h6 style =\"color:black\"> Review by  " + reviews[i]["username"] + " on " + reviews[i]["date"] + "</h6>";
         showMyReviews.innerHTML += "<h6>" + reviews[i]["title"] + "<h6>";
         showMyReviews.innerHTML += "<small>" + reviews[i]["text"] + "</small>";
         showMyReviews.innerHTML += "<hr style=\"height:2px;border-width:0;color:#568496;background-color:#568496\">";
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

  function displayRating() {
     var bar5 = document.getElementById("5bar");
     var bar4 = document.getElementById("4bar");
     var bar3 = document.getElementById("3bar");
     var bar2 = document.getElementById("2bar");
     var bar1 = document.getElementById("1bar");
     var avg = document.getElementById("avgRating");

     var fiveCnt = 0;
     var fourCnt  = 0;
     var threeCnt = 0;
     var twoCnt = 0;
     var oneCnt = 0;
     var totalRating = 0.0;
     var totalCnt  = 0;
     var avgRating;
     for(var i = 0; i < reviews.length; i++) {
        var rating = parseFloat(reviews[i]["rating"])
        totalRating += rating;
        totalCnt += 1;
        if(rating == 1) {
          oneCnt += 1;
        } else if(rating == 2) {
          twoCnt +=1;
        } else if(rating == 3) {
          threeCnt +=1;
        } else if(rating == 4) {
           fourCnt +=1;
        } else if(rating == 5){
           fiveCnt += 1;
        }
     }
     avgRating = (totalRating/totalCnt).toFixed(2);

     fiveCnt = fiveCnt/totalCnt*100;
     fourCnt = fourCnt/totalCnt*100;
     threeCnt = threeCnt/totalCnt*100;
     twoCnt = twoCnt/totalCnt*100;
     oneCnt = oneCnt/totalCnt*100;

     /*console.log(avgRating);
     console.log(fiveCnt);
     console.log(threeCnt);
     console.log(fourCnt);
     console.log(twoCnt);
     console.log(oneCnt);*/

     avg.innerHTML = "Average Rating: " + avgRating + "/5";
     bar5.innerHTML = "<div class=\"progress-bar\" role=\"progressbar\" style=\"width: " + fiveCnt +"%; background-color:#568496\" aria-valuenow=\"" + fiveCnt + "\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>";
     bar4.innerHTML = "<div class=\"progress-bar\" role=\"progressbar\" style=\"width: " + fourCnt +"%; background-color:#568496\"  aria-valuenow=\"" + fourCnt + "\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>";
     bar3.innerHTML = "<div class=\"progress-bar\" role=\"progressbar\" style=\"width: " + threeCnt +"%; background-color:#568496\" aria-valuenow=\"" + threeCnt + "\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>";
     bar2.innerHTML = "<div class=\"progress-bar\" role=\"progressbar\" style=\"width: " + twoCnt +"%; background-color:#568496\" aria-valuenow=\"" + twoCnt + "\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>";
     bar1.innerHTML = "<div class=\"progress-bar\" role=\"progressbar\" style=\"width: " + oneCnt +"%; background-color:#568496\" aria-valuenow=\"" + oneCnt + "\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>";
  }