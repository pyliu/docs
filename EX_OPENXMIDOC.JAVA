try {
                System.out.println("Load Xmi file.......");
                XMIFile file = XMIFile.load(filepath, XMIFile.DEFAULT, false);
                XMIContainer xmicontainer = new XMIContainer(file.getObjects().iterator());
                
                // 取得Top的XMIObject(UML:Model name = "pda.project")
                Collection topobj = xmicontainer.getTopObjects();
                ArrayList al = new ArrayList(topobj);
                // 取得Top Element(UML:Model)
                XMIObject top = (XMIObject)al.get(0);
                
                // 1.擷取ModelElement.taggedValue(裡面儲存title, intent...等)
                XMIObject meTaggedValue = (XMIObject)top.getXMIValueOfValue("ModelElement.taggedValue");
                // 取得裝TaggedValue的上層物件
                Value header_element = meTaggedValue.getXMIValue("TaggedValue");
                // 把裡面所有的value物件抓出來存到ArrayList中(方便一個個拿出來)
                Object gotHeader = header_element.getXMIValue();
                if (gotHeader instanceof Collection) { // 抓出的資訊是Collection
                    ArrayList heAl = new ArrayList((Collection)gotHeader);
                    for (int i=0; i<heAl.size(); i++) {
                        XMIObject temp = (XMIObject)heAl.get(i);
                        // 抓出tag的名稱和對應的值
                        String tag = (String)temp.getXMIValueOfValue("tag");
                        String value = (String)temp.getXMIValueOfValue("value");
                        setHeaderInfo(pa, tag, value);
                    }
                } else { // 抓出的資訊是XMIObject
                    String tag = (String)((XMIObject)gotHeader).getXMIValueOfValue("tag");
                    String value = (String)((XMIObject)gotHeader).getXMIValueOfValue("value");
                    setHeaderInfo(pa, tag, value);
                }
                
                // 2.擷取ModelElement.stereotype(裡面儲存related_patterns)
                XMIObject meStereotype = (XMIObject)top.getXMIValueOfValue("ModelElement.stereotype");
                Value stereo_elements = meStereotype.getXMIValue("Stereotype");
                if (stereo_elements != null) { // 有可能為null
                    // 擷取所有在ModelElement.stereotype之下的Stereotype
                    Object stereotype = stereo_elements.getXMIValue();
                    if (stereotype instanceof XMIObject) { // 只有一個Stereotype
                        XMIObject temp = (XMIObject)stereotype;
                        setRelatedPatterns(temp, pa);
                    }
                    else if (stereotype instanceof Collection) { // 有多個Stereotype
                        ArrayList seAl = new ArrayList((Collection)stereotype);
                        for (int i=0; i<seAl.size(); i++) {
                            XMIObject temp = (XMIObject)seAl.get(i);
                            setRelatedPatterns(temp, pa);
                        }
                    }
                }
                // 3.擷取Namespace.ownedElement(裡面儲存roles和relations等)
                // 取出ResourceDispatcher和XmiNotationFactory的instance
                ResourceDispatcher dispatcher = ResourceDispatcher.getInstance();
                XmiNotationFactory xmiFactory = XmiNotationFactory.getInstance();
                XMIObject owned_elements = (XMIObject)top.getXMIValueOfValue("Namespace.ownedElement");
                // 取得在Namespace.ownedElement之下的Class
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
                //處理Inheritance
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
                // 處理instantiate和delegate(都是UML:Dependency)
                Value allDependency = owned_elements.getXMIValue("Dependency");
                String name;
                if (allDependency != null) {
                    Object depends = allDependency.getXMIValue();
                    if (depends instanceof XMIObject) {
                        XMIObject temp = (XMIObject) depends;
                        name = (String) temp.getXMIValueOfValue("name");
                        if (name.equals("instantiate")) {   // 處理instantaite
                            MyInstantiate tempInstantiate = xmiFactory.createInstantiate(temp);
                            dispatcher.attach(tempInstantiate);
                            pa.getInstantiates().addElement(tempInstantiate);
                        } else {    // 處理delegate
                            MyDelegate tempDelegate = xmiFactory.createDelegate(temp);
                            dispatcher.attach(tempDelegate);
                            pa.getDelegates().addElement(tempDelegate);
                        }
                    } else if (depends instanceof Collection){
                        ArrayList dependAl = new ArrayList((Collection)depends);
                        for (int i=0; i<dependAl.size(); i++) {
                            XMIObject temp = (XMIObject)dependAl.get(i);
                            name = (String) temp.getXMIValueOfValue("name");
                            if (name.equals("instantiate")) {   // 處理instantaite
                                MyInstantiate tempInstantiate = xmiFactory.createInstantiate(temp);
                                dispatcher.attach(tempInstantiate);
                                pa.getInstantiates().addElement(tempInstantiate);
                            } else {    // 處理delegate
                                MyDelegate tempDelegate = xmiFactory.createDelegate(temp);
                                dispatcher.attach(tempDelegate);
                                pa.getDelegates().addElement(tempDelegate);
                            }
                        }
                    }
                }
                // 處理aggregate
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