The program analyzes HTML and finds a specific element, even after changes, using a set of extracted attributes.

The program must consume the original page to collect all the required information about the target element. 
Then the program should be able to find this element in diff-case HTML document that differs a bit from the original page. 
Original and diff-case HTML documents should be provided to the program in each run - no persistence is required.

**How to execute:**

java -cp ae-xml-analyzer-0.0.1.jar com.agileengine.Application _<original_file_path> <modified_file_path> <target_element_id>_

**Arguments description:**

_original_file_path_ - path to the original html file to analyze

_modified_file_path_ - path to the modified html file to analyze

_target_element_id_ - element id to be analyzed

**Execution example with samples:**

java -cp ae-xml-analyzer-0.0.1.jar com.agileengine.Application samples/sample-0-origin.html samples/sample-1-evil-gemini.html make-everything-ok-button