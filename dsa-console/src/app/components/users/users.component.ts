import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FormControl, Validators, FormBuilder, FormGroup} from '@angular/forms';
import {UsersService} from '../../services/users.service';
import {DatasetsService} from '../../services/datasets.service';
import { User, DataSet, BodyDataUser } from '../../models/profile';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  providers: [BodyDataUser]
})
export class UsersComponent implements OnInit {

  users:User[];
  displayedColumns:any;
  dataSource:any;
  domain: string;
  datasets: DataSet[];
  constructor(private userService: UsersService, private dialog: MatDialog, private bodyData:BodyDataUser, private route: ActivatedRoute, private datasetService: DatasetsService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.domain = params['domain'];
      this.userService.getUsers(this.domain).then(user => {
        this.users = user;
        this.displayedColumns = ['id', 'email', 'dataset', 'modification'];
        this.dataSource = new MatTableDataSource<User>(user);
      });
    });
    
  }

  /**
   * Create New User
   */
  openDialog4CreateUser() {
    this.datasetService.getDataSets(this.domain).then(ds => {
      this.datasets = ds;
      let dialogRef = this.dialog.open(CreateUserDialogComponent,{
        //height: '300px',
        width: '350px',
        data: {  id: "",username:"", ds:this.datasets, dialogStatus:"TitleCreate" }
      });
      dialogRef.afterClosed().subscribe(result => {
        if(result){
          this.bodyData.username=result.username;
          this.bodyData.dataset=result.ds;
          this.userService.setUser(this.domain,this.bodyData);
          //console.log('globalData in session:',sessionStorage.getItem('currentDomain'));
          //for reload the table
          setTimeout(()=>{  this.ngOnInit();},1000);
        }
      });
    });
  }

  /**
   * Edit A User
   */
  openDialog4EditUser(userId: string, username:string, ds:string) {
    console.log('get dsid:', userId);
    let dialogRef = this.dialog.open(CreateUserDialogComponent,{
      //height: '300px',
      width: '350px',
      data: {  id: userId, username: username, dataset: ds, dialogStatus: "TitleEdit" }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.bodyData.id=result.id;
        this.bodyData.username=result.username;
        this.userService.editUser(this.domain,this.bodyData.id,this.bodyData);
        setTimeout(()=>{  this.ngOnInit();},1000);
      }
      
    });
  }

  /**
   * Delete A User
   */
  openDialog4DeleteUser(userId: string, username:string){
    let dialogRef = this.dialog.open(CreateUserDialogComponent,{
      //height: '300px',
      width: '350px',
      data: {  id: userId, userDelete: username, dialogStatus:"TitleDelete" }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        this.userService.deleteUser(this.domain,result.id);
        setTimeout(()=>{  this.ngOnInit();},1000);
      }
      
    });
  }
  @ViewChild(MatSort) sort: MatSort;
  
  /**
   * Set the sort after the view init since this component will
   * be able to query its view for the initialized sort.
   */
  
  ngAfterViewInit() {
    //this.dataSource.sort = this.sort;
  }

}

@Component({
  selector : 'create-dataset-dialog',
  templateUrl : 'create-user-dialog.html',
  styleUrls: ['./users.component.css']
})
export class CreateUserDialogComponent {
  selectedDS: string;
  //constructor() {}
  constructor(public dialogRef: MatDialogRef<CreateUserDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  userControl = new FormControl('', [Validators.required]);
  
  getErrorMessage4user() {
    return this.userControl.hasError('required') ? 'You must enter a value' :
        //this.dataset.hasError('email') ? 'Not a valid email' :
            '';
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
