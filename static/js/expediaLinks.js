function updateExpedia(username, link) {
    await fetch('expediaLinks?link='+link, {method:'GET'})
}