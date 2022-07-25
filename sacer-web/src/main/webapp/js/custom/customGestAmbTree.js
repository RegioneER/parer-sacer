/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


var name;
               
var tree = $("#tree_GestAmbTree");
                              
tree.bind("select_node.jstree", function () {
    name =  $.jstree._focused().get_text();
    window.location = "Strutture.html?operation=loadNode&nodeName="+name; 
//name= selectedObj[0].textContent;
//selectedObj.attr("id") + selectedObj.attr("data"));
//window.location = "Strutture.html?operation=loadNode";
});
tree.bind("open_node.jstree", function () {
                    
    });
                
tree.on('loaded.jstree', function() {
    tree.jstree('open_all');
});  
