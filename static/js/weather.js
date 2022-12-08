async function currentWeather(longitude, latitude) {
    var temperature = document.getElementById("temperature");
    var windSpeed = document.getElementById("windspeed");
    var rain = document.getElementById("precipitation");
    console.log("Here in Get Current Weather");
    temperature.innerHTML = "";
    windSpeed.innerHTML = "";
    rain.innerHTML = "";
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var jsonObj = JSON.parse(xhttp.responseText);
            temperature.innerHTML = jsonObj.temperature +"&#176;C";
            windSpeed.innerHTML = jsonObj.windSpeed +" km/h";
            rain.innerHTML = jsonObj.rain.toFixed(2) +" mm";
        }
    }
    xhttp.open("POST", "/weather", true);
    xhttp.setRequestHeader("Content-type",
    "application/x-www-form-urlencoded");
    xhttp.send("lng="+longitude+"&lat="+latitude);
}