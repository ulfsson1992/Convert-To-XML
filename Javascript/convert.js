// print process.argv
var filename = process.argv[2]; // Read input file from first command line argument
var out = "out.xml";  // Default name is out.xml
if(process.argv[3] != null)     // Modify output file name if argument existing
  out = process.argv[3];

// Serializer and implementation for creating the xml dom and later saving it to file
var serializer = new (require('xmldom')).XMLSerializer;
var implementation = new (require('xmldom')).DOMImplementation;

// Create an empty xml dom
var document = implementation.createDocument('', '', null);

// Create root element
var root = document.createElement('people');
var currentPerson = root;

// Filestream init
var fs = require('fs'),
  readline = require('readline');

// Readstream interface
var rd = readline.createInterface({
  input: fs.createReadStream(filename)
});

// Read the file line by line
rd.on('line', function(line) {
  // Split the line to separate the arguments
  split = line.split("|");
  
  // Switch on the first argument for determining what type of information is described on the current line
  switch(split[0]) {
    case "P":   // Person
      // Create a person node and append it to root, change the current person to this
      var node = createNode("person", ["firstname", "lastname"], split)
      root.appendChild(node);
      currentPerson = node;
      break;
      
    case 'F':   // Family
      // If the parent node of the current person is not the root node it means that we are in another
      // family node and must reset to the correct parent node
      if(currentPerson.parentNode != root)
          currentPerson = currentPerson.parentNode;

      // For every attribute, make sure there exists a parent node
      if (currentPerson != null) {
          var node = createNode("family", ["name", "born"], split);
          currentPerson.appendChild(node);

          currentPerson = node;
      }
      break;
      
    case 'T':   // Telephone
      if (currentPerson != null) {
          currentPerson.appendChild(createNode("phone", ["mobile", "home"], split));
      }
      break;
      
    case 'A':   // Address
      if (currentPerson != null) {
          currentPerson.appendChild(createNode("adress", ["street", "city", "zipcode"], split));
      }
      break;
      
    default:
  }
});

// When the end of file has been reached, write the dom to file
rd.on('close', function writeXML() {
  document.appendChild(root);
  
  var fs = require('fs');

  fs.writeFile(
    out, 
    serializer.serializeToString(document), 
    function(error) {
      if (error) {
        console.log(error);
      } else {
        console.log("The file was saved to " + out);
      }
    }
  ); 
});

// Function creates a node by name with several nodes according to labels with their respective textnodes in values
function createNode(name, labels, values) {
  var node = document.createElement(name);
  for(var i = 1; i < values.length; i++) {
    var e = document.createElement(labels[i-1]);
    e.appendChild(document.createTextNode(values[i]));
    node.appendChild(e);
  }
  return node;
}