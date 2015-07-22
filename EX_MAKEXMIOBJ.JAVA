public ArrayList makeObjects() {
        // 存放所有的XMIObject
        ArrayList allObjs = new ArrayList();
        // 加入namespace
        Namespace ns = new Namespace("UML", "http://www.omg.org/UML13");
        // 取得project info資訊
        pInfo = (ProjectInfo)ResourceDispatcher.getInstance().getProjectInfo();
        // 取得所有Nodes
        Vector role = pInfo.getNodes();
        
        XMIObject project = new XMIObjectImpl("Model");
        XMIObject project_head = addProjectInfo();
        XMIObject related_patterns = addRelatedPatterns();
        XMIObject owned_element = new XMIObjectImpl("Namespace.ownedElement");
        
        project.addXMIValue("name", "pda.project", Value.DATA);
        project.addXMIValue("ModelElement.taggedValue", project_head, Value.OBJECT);
        project.addXMIValue("ModelElement.stereotype", related_patterns, Value.OBJECT);
        project.addXMIValue("Namespace.ownedElement", owned_element, Value.OBJECT);
        // 針對每個Node把他加入owned_element裡
        for(int i=0;i<role.size();i++) {
            Hashtable h=((MyNode)role.elementAt(i)).getAttributes();
            Vector records = ((MyNode)role.elementAt(i)).getRelatedRoleRecords();
            String className = ((MyNode)role.elementAt(i)).getName();
            XMIObject aClass = addRole(h, className, records);
            String participant = ((MyNode)role.elementAt(i)).getParticipant();
            aClass.addXMIValue("ModelElement.taggedValue", addParticipant(participant), Value.OBJECT);
            
            XMIObject feature = new XMIObjectImpl("Classifier.feature");
            aClass.addXMIValue("Classifier.feature", feature, Value.OBJECT);
            MyNode currentNode = (MyNode)role.elementAt(i);
            if((currentNode.getFunctionLabels())!=null)
                addMemberFunction(currentNode, feature);
            if((currentNode.getVariableLabels())!=null)
                addMemberVariable(currentNode, feature);
            // 把目前這個Node加入owned_element中
            owned_element.addXMIValue("Class", aClass, Value.OBJECT);
        }
        // 加入relations
        // 加入Inheritance
        addInheritance(owned_element);
        // 加入Instantiate
        addInstantiate(owned_element);
        // 加入Delegate
        addDelegate(owned_element);
        // 加入Aggregate
        addAggregate(owned_element);
        
        allObjs.add(project);
        assignNamespace(ns, allObjs);
        return allObjs;
    }