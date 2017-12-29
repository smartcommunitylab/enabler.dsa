
function populateDS(entries) {
	var dropdown = document.getElementById("domains");
	
    var selectDatasets = document.getElementById("datasets"); 
    selectDatasets.innerHTML = "";
    
    var datasets = entries[dropdown.value];
    for(var i = 0; i < datasets.length; i++) {
      var opt = datasets[i];
      var el = document.createElement("option");
      el.textContent = opt;
      el.value = opt;
      selectDatasets.appendChild(el);
    }
}

function storeAfterDash() {
	url = window.location.href;

	var splitted = url.split("#");
	if (splitted.length > 1) {
		sessionStorage.setItem("postDashParameters", splitted[1]);
	} else {
		sessionStorage.setItem("postDashParameters", "");
	}
	window.location.href = "login";
}

function restoreAfterDash() {
	var url = sessionStorage.getItem("postDashParameters");
	var hidden = document.getElementById("postDashParameters");
	hidden.value = url;		
}

function init(entries) {
	populateDS(entries);
	restoreAfterDash();
}