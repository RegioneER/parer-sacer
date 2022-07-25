/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var name;

$("#gestCatTiUdTree_createBtn").click( function() {
    window.location = "Strutture.html?operation=createNode";
});
$("#gestCatTiUdTree_renameBtn").click( function() {
    window.location = "Strutture.html?operation=updateNode";
});
$("#gestCatTiUdTree_deleteBtn").click( function() {
    window.location = "Strutture.html?operation=confRemove";
});
                
var tree = $("#tree_GestCatTiUdTree");
tree.on('loaded.jstree', function() {
    tree.jstree('open_all');
});  
                
//funzione di visualizzazione dettaglio nodo selezionato
tree.bind("select_node.jstree", function (e, data) {
	name =  data.node.text;
    window.location = "Strutture.html?operation=loadNode&nodeName="+name; 
});
                
//funzione per gestione drag 'n' drop
tree.bind("move_node.jstree",function(e, data){                    
//    if(nodeDestId === "tree_GestCatTiUdTree"){
//        nodeDestId = "0";
//    }
	nodeDestId = data.node.parent;          
    nodeId= data.node.id;
    window.location = "Strutture.html?operation=moveNode&nodeId="+nodeId+"&nodeDestId="+nodeDestId; 
});
                