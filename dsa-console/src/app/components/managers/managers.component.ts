import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FormControl, Validators, FormBuilder, FormGroup} from '@angular/forms';
import {ManagersService} from '../../services/managers.service';
import { Manager, BodyDataManager } from '../../models/profile';
import {ActivatedRoute} from '@angular/router';

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

  constructor(private managerService: ManagersService, private dialog: MatDialog, private bodydata: BodyDataManager, private route: ActivatedRoute) { }
  
  ngOnInit() {
    this.route.params.subscribe(params => {
      this.domain = params['domain'];
      this.managerService.getManagers(this.domain).then(mng => {
        this.managers = mng;
        this.displayedColumns = ['id', 'email','modification'];
        this.dataSource = new MatTableDataSource<Manager>(mng);
      });
    });
  }
  
  /**
   * Create New Manager
   */
  openDialog4CreateManager() {
    //var bodydata: BodyData;
    let dialogRef = this.dialog.open(CreateManagerDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  username:"", mngDelete: "", dialogStatus:"TitleCreate" }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.bodydata.username=result.username;
        this.bodydata.owner=true;
        this.managerService.setManager(this.domain,this.bodydata);
        //for reload the table
        setTimeout(()=>{  this.ngOnInit();},1000);
      }
    });
  }

  /**
   * Edit A Manager
   */
  openDialog4EditManager(mngId: string, email: string) {
    let dialogRef = this.dialog.open(CreateManagerDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id: mngId, email: email, dialogStatus:"TitleEdit" }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        console.log('edit dialog was closed and result:',result);
        this.bodydata.id=result.toString();
        this.managerService.editManager(this.domain,this.bodydata.id,this.bodydata);
      }
      
    });
  }

  /**
   * Delete A Manager
   */
  openDialog4DeleteManager(mngId: string, mngUnsername: string){
    let dialogRef = this.dialog.open(CreateManagerDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id: mngId, username:"", mngDelete: mngUnsername, dialogStatus:"TitleDelete" }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        console.log('delete dialog was closed and result:',result);
        this.managerService.deleteManager(this.domain,result.id);
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
  
  mngControl = new FormControl('', [Validators.required]);
  
  getErrorMessage4mng() {
    return this.mngControl.hasError('required') ? 'You must enter a value' :
        //this.dataset.hasError('email') ? 'Not a valid email' :
            '';
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}