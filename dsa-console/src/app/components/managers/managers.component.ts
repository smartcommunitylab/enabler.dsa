import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {ManagersService} from '../../services/managers.service';
import { Manager, BodyDataManager } from '../../models/profile';

@Component({
  selector: 'app-managers',
  templateUrl: './managers.component.html',
  styleUrls: ['./managers.component.css'],
  providers: [BodyDataManager]
})
export class ManagersComponent implements OnInit {
  managers:Manager[];
  displayedColumns:any;
  dataSource:any;
  dialogStatus = '';
  domain: string;

  constructor(private managerService: ManagersService, private dialog: MatDialog, private bodydata: BodyDataManager,) { }
  
  ngOnInit() {
    this.managerService.getManagers('test1').then(mng => {
      this.managers = mng;
      this.displayedColumns = ['id', 'email','modification'];
      this.dataSource = new MatTableDataSource<Manager>(mng);
    });
  }
  
  /**
   * Create New Manager
   */
  openDialog4CreateManager() {
    //var bodydata: BodyData;
    let dialogRef = this.dialog.open(CreateManagerDialogComponent,{
      height: '300px',
      width: '350px',
      data: {  id: this.bodydata.id, dialogStatus:"Create" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed',result);
      if(result){
        //this.config = result;
        this.bodydata.id=result.toString();
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.managerService.setManager('test1',this.bodydata);
        //console.log('globalData in session:',sessionStorage.getItem('currentDomain'));
      }
    });
  }

  /**
   * Edit A Manager
   */
  openDialog4EditManager(dsId: string) {
    console.log('get dsid:', dsId);
    let dialogRef = this.dialog.open(CreateManagerDialogComponent,{
      height: '300px',
      width: '350px',
      data: {  id: dsId, dialogStatus:"Edit" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Edit dialog was closed',result);
      if(result){
        //this.config = result;
        this.bodydata.id=result.toString();
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        //this.datasetService.editDataset('test1',this.bodydata.id,this.bodydata);
        
      }
      
    });
  }

  /**
   * Delete A Manager
   */
  openDialog4DeleteManager(dsId: string){
    let dialogRef = this.dialog.open(CreateManagerDialogComponent,{
      height: '300px',
      width: '350px',
      data: {  id: dsId, dialogStatus:"Delete" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Delete dialog was closed',result);
      if(result){
        //this.config = result;
        //this.bodydata.id=result.toString();
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        //this.datasetService.deleteDataset('test1',result.toString());
        
      }
      
    });
  }

  submitted = false;
  
  onSubmit() { this.submitted = true; }
}

@Component({
  selector : 'create-manager-dialog',
  templateUrl : 'create-manager-dialog.html',
  styleUrls: ['./managers.component.css']
})
export class CreateManagerDialogComponent {
  //constructor() {}
  constructor(public dialogRef: MatDialogRef<CreateManagerDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  

  onNoClick(): void {
    this.dialogRef.close();
  }
}