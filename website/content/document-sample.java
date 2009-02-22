//START TFILE-OPEN
TFile file = ...
file.open(new LineProcessor() {
  public void process(String line) {
    System.out.println("Read line: " + line);
  }
});
//END TFILE-OPEN

//START MANAGE-RESOURCE
TFile file = ...
file.open(new FileIoProcessor() {
  public void process(FileIoResource io) throws IOException {
    PrintWriter printer = io.printWriter();
    printer.println("line one");
    printer.println("line two");
  }
});
//END MANAGE-RESOURCE

//START IOFACTORY
TFile file = ...
PrintWriter printer = file.io().printWriter();
try {
  printer.println("line one");
  printer.println("line two");
} catch (IOException e) {
  // ... exception handling code here
} finally {
  printer.close();
}
//END IOFACTORY