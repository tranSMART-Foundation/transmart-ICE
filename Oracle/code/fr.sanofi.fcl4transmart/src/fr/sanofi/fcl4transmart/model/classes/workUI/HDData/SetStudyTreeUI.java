/*******************************************************************************
 * Copyright (c) 2012 Sanofi-Aventis Recherche et Developpement.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *    Sanofi-Aventis Recherche et Developpement - initial API and implementation
 ******************************************************************************/
package fr.sanofi.fcl4transmart.model.classes.workUI.HDData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import fr.sanofi.fcl4transmart.controllers.listeners.clinicalData.StudyContentProvider;
import fr.sanofi.fcl4transmart.controllers.listeners.HDData.SetStudyTreeListener;
import fr.sanofi.fcl4transmart.model.classes.StudyTree;
import fr.sanofi.fcl4transmart.model.classes.TreeNode;
import fr.sanofi.fcl4transmart.model.classes.dataType.HDDataItf;
import fr.sanofi.fcl4transmart.model.interfaces.DataTypeItf;
import fr.sanofi.fcl4transmart.model.interfaces.WorkItf;
/**
 *This class allows the creation of the composite to set the category code attributer of the sample to subject mapping file
 */
public class SetStudyTreeUI implements WorkItf{
	protected DataTypeItf dataType;
	protected TreeViewer viewer;
	protected StudyTree studyTree;
	protected Combo newLabelField;
	protected Text newTextField;
	protected TreeNode root;
	protected Listener setTreeListener;
	public SetStudyTreeUI(DataTypeItf dataType){
		this.dataType=dataType;
		this.setListeners();
	}
	protected void setListeners(){
		this.setTreeListener=new SetStudyTreeListener(this.dataType, this);
	}
	@Override
	public Composite createUI(Composite parent){
		Composite composite=new Composite(parent, SWT.NONE);
		GridLayout gd=new GridLayout();
		gd.numColumns=1;
		gd.horizontalSpacing=0;
		gd.verticalSpacing=0;
		composite.setLayout(gd);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ScrolledComposite scroller=new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		scroller.setLayoutData(new GridData(GridData.FILL_BOTH));
		gd=new GridLayout();
		gd.numColumns=1;
		gd.horizontalSpacing=0;
		gd.verticalSpacing=0;
		scroller.setLayout(gd);
		scroller.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite scrolledComposite=new Composite(scroller, SWT.NONE);
		scroller.setContent(scrolledComposite); 
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		scrolledComposite.setLayout(layout);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite body=new Composite(scrolledComposite, SWT.NONE);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		gd=new GridLayout();
		gd.numColumns=2;
		gd.horizontalSpacing=5;
		gd.verticalSpacing=5;
		body.setLayout(gd);

		this.root=new TreeNode(this.dataType.getStudy().toString(), null, false);
		this.initiate();
		this.studyTree=new StudyTree(root);
		
		viewer = new TreeViewer(body, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new StudyContentProvider());
		viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		viewer.setInput(this.studyTree);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment=SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace=true;
		gridData.heightHint=300;
		gridData.widthHint=250;
		this.viewer.getControl().setLayoutData(gridData);
		
		Composite leftPart=new Composite(body, SWT.NONE);
		gd=new GridLayout();
		gd.numColumns=1;
		gd.horizontalSpacing=5;
		gd.verticalSpacing=10;
		leftPart.setLayout(gd);
		leftPart.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite freeTextPart=new Composite(leftPart, SWT.NONE);
		gd=new GridLayout();
		gd.numColumns=2;
		gd.horizontalSpacing=5;
		gd.verticalSpacing=5;
		freeTextPart.setLayout(gd);

		Label newTextLabel=new Label(freeTextPart, SWT.NONE);
		newTextLabel.setText("Free text");
		newTextLabel.setLayoutData(new GridData());
		gridData = new GridData();
		gridData.widthHint=120;
		newTextLabel.setLayoutData(gridData);
		
		this.newTextField=new Text(freeTextPart, SWT.BORDER);
		gridData = new GridData();
		gridData.widthHint=110;
		this.newTextField.setLayoutData(gridData);
		
		Button addText=new Button(freeTextPart, SWT.PUSH);
		addText.setText("Add free text");
		
		
		addText.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				IStructuredSelection selection=(IStructuredSelection)viewer.getSelection();
				TreeNode node;
				if(selection.iterator().hasNext()){
					node=(TreeNode)selection.iterator().next();
				}
				else{
					displayMessage("Select a node first");
					return;
				}
				if(node.hasChildren()){
					displayMessage("A node can only have one child");
					return;
				}
				if(newTextField.getText().compareTo("")==0){
					displayMessage("Choose a node name first");
					return;
				}
				node.addChild(new TreeNode(newTextField.getText(), node, false));
				viewer.setExpandedState(node, true);
				viewer.refresh();
			}
		});
		
		Composite propPart=new Composite(leftPart, SWT.NONE);
		gd=new GridLayout();
		gd.numColumns=2;
		gd.horizontalSpacing=5;
		gd.verticalSpacing=5;
		propPart.setLayout(gd);
		
		Label newLabelLabel=new Label(propPart, SWT.NONE);
		newLabelLabel.setText("Choose property");
		gridData = new GridData();
		gridData.widthHint=120;
		newLabelLabel.setLayoutData(gridData);
		
		this.newLabelField=new Combo(propPart, SWT.DROP_DOWN | SWT.BORDER | SWT.WRAP);
	    this.newLabelField.addListener(SWT.KeyDown, new Listener(){ 
	    	public void handleEvent(Event event) { 
	    		event.doit = false; 
	    	} 
    	}); 
		//add the different properties
		this.newLabelField.add("PLATFORM");
		this.newLabelField.add("TISSUETYPE");
		this.newLabelField.add("ATTR1");
		this.newLabelField.add("ATTR2");
		gridData = new GridData();
		gridData.widthHint=100;
		this.newLabelField.setLayoutData(gridData);
		
		Button addChild=new Button(propPart, SWT.PUSH);
		addChild.setText("Add property");
		
		addChild.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				IStructuredSelection selection=(IStructuredSelection)viewer.getSelection();
				TreeNode node;
				if(selection.iterator().hasNext()){
					node=(TreeNode)selection.iterator().next();
				}
				else{
					displayMessage("Select a node first");
					return;
				}
				if(node.hasChildren()){
					displayMessage("A node can only have one child");
					return;
				}
				if(newLabelField.getText().compareTo("")==0){
					displayMessage("Choose a property first");
					return;
				}
				node.addChild(new TreeNode(newLabelField.getText(), node, false));
				viewer.setExpandedState(node, true);
				viewer.refresh();
			}
		});
		
		@SuppressWarnings("unused")
		Label space2=new Label(leftPart, SWT.NONE);
		
		Button remove=new Button(leftPart,SWT.PUSH);
		remove.setText("Remove a node");
		remove.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				IStructuredSelection selection=(IStructuredSelection)viewer.getSelection();
				TreeNode node;
				if(selection.iterator().hasNext()){
					node=(TreeNode)selection.iterator().next();
				}
				else{
					displayMessage("Select a node first");
					return;
				}
				if(node.getParent()==null){
					displayMessage("Root can not be removed");
					return;
				}
				node.getParent().removeChild(node);
				viewer.refresh();
			}
		});
		
		Button ok=new Button(scrolledComposite, SWT.PUSH);
		ok.setText("OK");
		ok.addListener(SWT.Selection, this.setTreeListener);
		

		scrolledComposite.setSize(scrolledComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		return composite;
	}
	public void displayMessage(String message){
	    int style = SWT.ICON_INFORMATION | SWT.OK;
	    MessageBox messageBox = new MessageBox(new Shell(), style);
	    messageBox.setMessage(message);
	    messageBox.open();
	}
	public TreeNode getRoot(){
		return this.root;
	}
	public void initiate(){
		File stsmf=((HDDataItf)this.dataType).getMappingFile();
		if(stsmf==null) return;
		String category;
		try{
			BufferedReader br = new BufferedReader(new FileReader(stsmf));
			String line=br.readLine();
			line=br.readLine();
			category=line.split("\t", -1)[8];
			br.close();
		}
		catch(Exception e){
			displayMessage("Error: "+e.getLocalizedMessage());
			e.printStackTrace();
			return;
		}
		if(category.compareTo("")==0){
			return;
		}
		String[] nodes=category.split("\\+", -1);
		TreeNode node=this.root;
		for(int i=0; i<nodes.length; i++){
			TreeNode child=new TreeNode(nodes[i].replace('_', ' '), node, false);
			node.addChild(child);
			node=child;
		}
	}
	@Override
	public boolean canCopy() {
		return false;
	}
	@Override
	public boolean canPaste() {
		return false;
	}
	@Override
	public Vector<Vector<String>> copy() {
		return null;
	}
	@Override
	public void paste(Vector<Vector<String>> data) {
		// nothing to do
		
	}
	@Override
	public void mapFromClipboard(Vector<Vector<String>> data) {
		// nothing to do
		
	}
}
