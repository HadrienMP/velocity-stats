document.getElementById("period").onchange = function () {
    document.getElementById("analysis-form").submit();
};
Sortable.create(document.getElementById("tickets"), {
    animation: 150,
    ghostClass: 'moving'
});

let toto = parseInt($("#container h2 span").text());
let monthHeight = toto * $("#tickets li").outerHeight();
let thisMonth = document.getElementById("this-month");
let nextMonth = document.getElementById("next-month");
thisMonth.style.height = monthHeight + "px";
let thisMonthTop = parseInt(window.getComputedStyle(thisMonth)
    .getPropertyValue("top")
    .replace("px", ""));
nextMonth.style.top = (monthHeight + thisMonthTop) + "px";
nextMonth.style.height = monthHeight + "px";