const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl,
    updateTable: function () {
        $.get(userAjaxUrl, updateTableWithData)
    }
}

function setEnable(checkBox, id) {
    let enabled = checkBox.is(":checked")
    $.ajax({
        url: userAjaxUrl + id,
        type: "POST",
        data: "enable=" + enabled
    }).done(function () {
        checkBox.closest("tr").attr("user-enabled", enabled)
        // updateTable();
        successNoty(enabled);
    }).fail($(checkBox).prop("checked", !enabled));
}

// function setEnable() {
//     enable($(".enable"), $(".enable").closest('tr').attr("id"))
// }

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    );
});