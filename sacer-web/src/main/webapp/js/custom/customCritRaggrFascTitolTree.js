/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
              
var tree = $("#tree_TitolariTree");

tree.on("loaded.jstree", function() {
    tree.jstree('open_all');
    tree.off("contextmenu.jstree"); // disattiva il plugin    
});  
                
tree.bind("select_node.jstree", function (e, data) {
		var id_voce_titol =  data.node.id;
		
	    $.post("CriteriRaggrFascicoli.html", {
	        operation: "triggerTitolarioDetailCd_composito_voce_titolOnTrigger",
	        Id_voce_titol: id_voce_titol
	    }).done(function (data) {
	        CAjaxDataFormWalk(data);
	    });   
});
                