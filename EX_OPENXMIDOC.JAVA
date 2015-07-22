try {
                System.out.println("Load Xmi file.......");
                XMIFile file = XMIFile.load(filepath, XMIFile.DEFAULT, false);
                XMIContainer xmicontainer = new XMIContainer(file.getObjects().iterator());
                
                // ���oTop��XMIObject(UML:Model name = "pda.project")
                Collection topobj = xmicontainer.getTopObjects();
                ArrayList al = new ArrayList(topobj);
                // ���oTop Element(UML:Model)
                XMIObject top = (XMIObject)al.get(0);
                
                // 1.�^��ModelElement.taggedValue(�̭��x�stitle, intent...��)
                XMIObject meTaggedValue = (XMIObject)top.getXMIValueOfValue("ModelElement.taggedValue");
                // ���o��TaggedValue���W�h����
                Value header_element = meTaggedValue.getXMIValue("TaggedValue");
                // ��̭��Ҧ���value�����X�Ӧs��ArrayList��(��K�@�ӭӮ��X��)
                Object gotHeader = header_element.getXMIValue();
                if (gotHeader instanceof Collection) { // ��X����T�OCollection
                    ArrayList heAl = new ArrayList((Collection)gotHeader);
                    for (int i=0; i<heAl.size(); i++) {
                        XMIObject temp = (XMIObject)heAl.get(i);
                        // ��Xtag���W�٩M��������
                        String tag = (String)temp.getXMIValueOfValue("tag");
                        String value = (String)temp.getXMIValueOfValue("value");
                        setHeaderInfo(pa, tag, value);
                    }
                } else { // ��X����T�OXMIObject
                    String tag = (String)((XMIObject)gotHeader).getXMIValueOfValue("tag");
                    String value = (String)((XMIObject)gotHeader).getXMIValueOfValue("value");
                    setHeaderInfo(pa, tag, value);
                }
                
                // 2.�^��ModelElement.stereotype(�̭��x�srelated_patterns)
                XMIObject meStereotype = (XMIObject)top.getXMIValueOfValue("ModelElement.stereotype");
                Value stereo_elements = meStereotype.getXMIValue("Stereotype");
                if (stereo_elements != null) { // ���i�ରnull
                    // �^���Ҧ��bModelElement.stereotype���U��Stereotype
                    Object stereotype = stereo_elements.getXMIValue();
                    if (stereotype instanceof XMIObject) { // �u���@��Stereotype
                        XMIObject temp = (XMIObject)stereotype;
                        setRelatedPatterns(temp, pa);
                    }
                    else if (stereotype instanceof Collection) { // ���h��Stereotype
                        ArrayList seAl = new ArrayList((Collection)stereotype);
                        for (int i=0; i<seAl.size(); i++) {
                            XMIObject temp = (XMIObject)seAl.get(i);
                            setRelatedPatterns(temp, pa);
                        }
                    }
                }
                // 3.�^��Namespace.ownedElement(�̭��x�sroles�Mrelations��)
                // ���XResourceDispatcher�MXmiNotationFactory��instance
                ResourceDispatcher dispatcher = ResourceDispatcher.getInstance();
                XmiNotationFactory xmiFactory = XmiNotationFactory.getInstance();
                XMIObject owned_elements = (XMIObject)top.getXMIValueOfValue("Namespace.ownedElement");
                // ���o�bNamespace.ownedElement���U��Class
                Value allclass = owned_elements.getXMIValue("Class");
                if (allclass != null) {
                    Object classes = allclass.getXMIValue();
                    if (classes instanceof XMIObject) {
                        MyNode tempNode = xmiFactory.createNode((XMIObject) classes);
                        dispatcher.attach(tempNode);
                        pa.getNodes().addElement(tempNode);
                    } else if (classes instanceof Collection){
                        ArrayList classAl = new ArrayList((Collection)classes);
                        for (int i=0; i<classAl.size(); i++) {
                            XMIObject temp = (XMIObject)classAl.get(i);
                            MyNode tempNode = xmiFactory.createNode(temp);
                            dispatcher.attach(tempNode);
                            pa.getNodes().addElement(tempNode);
                        }
                    }
                }
                //�B�zInheritance
                Value allinher = owned_elements.getXMIValue("Generalization");
                if (allinher != null) {
                    Object inhers = allinher.getXMIValue();
                    if (inhers instanceof XMIObject) {
                        MyInheritance tempInheritance = xmiFactory.createInheritance((XMIObject)inhers);
                        pa.getInheritances().addElement(tempInheritance);
                    } else if (inhers instanceof Collection){
                        ArrayList inherAl = new ArrayList((Collection)inhers);
                        for (int i=0; i<inherAl.size(); i++) {
                            XMIObject temp = (XMIObject)inherAl.get(i);
                            MyInheritance tempInheritance = xmiFactory.createInheritance(temp);
                            pa.getInheritances().addElement(tempInheritance);
                        }
                    }
                }
                // �B�zinstantiate�Mdelegate(���OUML:Dependency)
                Value allDependency = owned_elements.getXMIValue("Dependency");
                String name;
                if (allDependency != null) {
                    Object depends = allDependency.getXMIValue();
                    if (depends instanceof XMIObject) {
                        XMIObject temp = (XMIObject) depends;
                        name = (String) temp.getXMIValueOfValue("name");
                        if (name.equals("instantiate")) {   // �B�zinstantaite
                            MyInstantiate tempInstantiate = xmiFactory.createInstantiate(temp);
                            dispatcher.attach(tempInstantiate);
                            pa.getInstantiates().addElement(tempInstantiate);
                        } else {    // �B�zdelegate
                            MyDelegate tempDelegate = xmiFactory.createDelegate(temp);
                            dispatcher.attach(tempDelegate);
                            pa.getDelegates().addElement(tempDelegate);
                        }
                    } else if (depends instanceof Collection){
                        ArrayList dependAl = new ArrayList((Collection)depends);
                        for (int i=0; i<dependAl.size(); i++) {
                            XMIObject temp = (XMIObject)dependAl.get(i);
                            name = (String) temp.getXMIValueOfValue("name");
                            if (name.equals("instantiate")) {   // �B�zinstantaite
                                MyInstantiate tempInstantiate = xmiFactory.createInstantiate(temp);
                                dispatcher.attach(tempInstantiate);
                                pa.getInstantiates().addElement(tempInstantiate);
                            } else {    // �B�zdelegate
                                MyDelegate tempDelegate = xmiFactory.createDelegate(temp);
                                dispatcher.attach(tempDelegate);
                                pa.getDelegates().addElement(tempDelegate);
                            }
                        }
                    }
                }
                // �B�zaggregate
                Value allAssociation = owned_elements.getXMIValue("Association");
                if (allAssociation != null) {
                    Object assocs = allAssociation.getXMIValue();
                    if (assocs instanceof XMIObject) {
                        XMIObject temp = (XMIObject) assocs;
                        MyAggregate tempAggregate = xmiFactory.createAggregate(temp);
                        dispatcher.attach(tempAggregate);
                        pa.getAggregates().addElement(tempAggregate);
                    } else if (assocs instanceof Collection){
                        ArrayList assocAl = new ArrayList((Collection)assocs);
                        for (int i=0; i<assocAl.size(); i++) {
                            XMIObject temp = (XMIObject) assocAl.get(i);
                            MyAggregate tempAggregate = xmiFactory.createAggregate(temp);
                            dispatcher.attach(tempAggregate);
                            pa.getAggregates().addElement(tempAggregate);
                        }
                    }
                }
                System.out.println("Load Xmi file finish");
            } catch (Exception e) { e.printStackTrace(); }