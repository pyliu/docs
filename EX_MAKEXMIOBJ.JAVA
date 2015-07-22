public ArrayList makeObjects() {
        // �s��Ҧ���XMIObject
        ArrayList allObjs = new ArrayList();
        // �[�Jnamespace
        Namespace ns = new Namespace("UML", "http://www.omg.org/UML13");
        // ���oproject info��T
        pInfo = (ProjectInfo)ResourceDispatcher.getInstance().getProjectInfo();
        // ���o�Ҧ�Nodes
        Vector role = pInfo.getNodes();
        
        XMIObject project = new XMIObjectImpl("Model");
        XMIObject project_head = addProjectInfo();
        XMIObject related_patterns = addRelatedPatterns();
        XMIObject owned_element = new XMIObjectImpl("Namespace.ownedElement");
        
        project.addXMIValue("name", "pda.project", Value.DATA);
        project.addXMIValue("ModelElement.taggedValue", project_head, Value.OBJECT);
        project.addXMIValue("ModelElement.stereotype", related_patterns, Value.OBJECT);
        project.addXMIValue("Namespace.ownedElement", owned_element, Value.OBJECT);
        // �w��C��Node��L�[�Jowned_element��
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
            // ��ثe�o��Node�[�Jowned_element��
            owned_element.addXMIValue("Class", aClass, Value.OBJECT);
        }
        // �[�Jrelations
        // �[�JInheritance
        addInheritance(owned_element);
        // �[�JInstantiate
        addInstantiate(owned_element);
        // �[�JDelegate
        addDelegate(owned_element);
        // �[�JAggregate
        addAggregate(owned_element);
        
        allObjs.add(project);
        assignNamespace(ns, allObjs);
        return allObjs;
    }