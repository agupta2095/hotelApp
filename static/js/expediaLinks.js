async function updateExpedia(link, hotelName) {
     console.log("Here in update Expedia Page");
    await fetch('/expediaLinks?link='+link +'&hotelName=' + hotelName, {method:'GET'})
}