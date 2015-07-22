        ArrayList xmiobjs = MakeXmiObj.getInstance().makeObjects();
        XMIFile file = new XMIFile(filePath);
        file.add(new Namespace("UML", "http://www.omg.org/UML13"));
        file.setExporter("Pattern Design Assistant");
        file.setExporterVersion("0.5");
        file.setShortDescription("Structure NOTE");
        try {
            file.write(xmiobjs.iterator(), XMIFile.DEFAULT);
            System.out.println("Xmi File saved !");
        } catch (Exception xmie) { xmie.printStackTrace(); }