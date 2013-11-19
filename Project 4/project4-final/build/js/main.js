var xmlhttp;
var current_position = -1;
var num_suggestions = 0;

function loadXMLDoc(url, cfunc) {
    if (window.XMLHttpRequest) {
        xmlhttp=new XMLHttpRequest();
    }
    else {
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange=cfunc;
    xmlhttp.open("GET",url,true);
    xmlhttp.send();
}

function suggestCallback() {
    if (xmlhttp.readyState==4 && xmlhttp.status==200) {
        var xmlDoc = xmlhttp.responseXML;
        var suggestions = xmlDoc.getElementsByTagName("suggestion");
        displaySuggestions(suggestions);
    }
}

function displaySuggestions(suggestions) {
    $("#suggestions").empty();
    for (var i=0;i<suggestions.length;i++) {
        $("#suggestions").append("<div class='suggestion'>"+suggestions[i].getAttribute("data")+"</div>");    
    }
    initializeEventHandlers();
    num_suggestions = suggestions.length;
    current_position = -1;
}

function queryOnKeyup(query) {
    var encoded_query = encodeURIComponent(query);
    loadXMLDoc("suggest?q="+encoded_query,suggestCallback);
}

function initializeEventHandlers() {
    $(".suggestion").hover(
        function() {
            current_position=-1;  
            $(".suggestion").css("background-color","white");
            $(".suggestion").css("color","black");            
            $(this).css("background-color","#3366cc");
            $(this).css("color","white");
        },
        function() {
            $(this).css("background-color","white");
            $(this).css("color","black");
            current_position=-1;        
        }
    );
    $(".suggestion").click(
        function() {
            var query = $(this).text();
            $("#keyword_input").val(query);
            loadXMLDoc("suggest?q="+encodeURIComponent(query),suggestCallback);
            $("#keyword_input").focus();
        }
    )
}

function incrementCurrentPosition() {
    if (num_suggestions!=0) {
        current_position++;
        if (current_position >= num_suggestions) current_position = num_suggestions-1;
        else updateCurrentHighlight();
    }
}

function decrementCurrentPosition() {
    if (num_suggestions!=0) {
        current_position--;
        if (current_position < -1) current_position = -1;
        else updateCurrentHighlight();
    }
}

function updateCurrentHighlight() {
    $(".suggestion").css("background-color","white");
    $(".suggestion").css("color","black");
    if (current_position > -1) {
        var suggestion = $(".suggestion")[current_position];
        $(suggestion).css("background-color","#3366cc");
        $(suggestion).css("color","white");
    }
    if (current_position >= 0) {
        var query = $($(".suggestion")[current_position]).text();
        $("#keyword_input").val(query);
    }
}

$(document).ready(function() {
    $("input").attr("autocomplete","off");

    $("#keyword_input").keyup(
        function(e) {
            //Down Arrow
            if (e.keyCode == 40) {
                incrementCurrentPosition();
            }
            //Up Arrow
            else if (e.keyCode == 38) {
                decrementCurrentPosition();
            }
            else {
                var query = $(this).val();
                var encoded_query = encodeURIComponent(query);
                loadXMLDoc("suggest?q="+encoded_query,suggestCallback);
            }
        }
    );
});