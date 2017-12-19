import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {UsersService} from '../../services/users.service';
import { User, Configuration, BodyDataUser } from '../../models/profile';

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
  constructor(private userService: UsersService, private dialog: MatDialog, private bodyData:BodyDataUser) { 

  }

  ngOnInit() {
    this.userService.getUsers('test1').then(user => {
      this.users = user;
      this.displayedColumns = ['id', 'email', 'dataset', 'modification'];
      this.dataSource = new MatTableDataSource<User>(user);
    });
  }

  /**
   * Create New User
   */
  openDialog4CreateUser() {
    //var bodydata: BodyData;
    let dialogRef = this.dialog.open(CreateUserDialogComponent,{
      height: '300px',
      width: '350px',
      data: {  id: this.bodyData.id, dialogStatus:"Create" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed',result);
      if(result){
        //this.config = result;
        this.bodyData.id=result.toString();
        //console.log('The dialog was closed and this.bodyData:',this.bodyData);
        this.userService.setUser('test1',this.bodyData);
        //console.log('globalData in session:',sessionStorage.getItem('currentDomain'));
      }
    });
  }

  /**
   * Edit A User
   */
  openDialog4EditUser(userId: string, email:string, ds:string) {
    console.log('get dsid:', userId);
    let dialogRef = this.dialog.open(CreateUserDialogComponent,{
      height: '300px',
      width: '350px',
      data: {  id: userId, email: email, dataset: ds, dialogStatus: "Edit" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Edit dialog was closed',result);
      if(result){
        this.bodyData.id=result.toString();
        //console.log('The dialog was closed and this.bodyData:',this.bodyData);
        this.userService.editUser('test1',this.bodyData.id,this.bodyData);
      }
      
    });
  }

  /**
   * Delete A User
   */
  openDialog4DeleteUser(dsId: string){
    let dialogRef = this.dialog.open(CreateUserDialogComponent,{
      height: '300px',
      width: '350px',
      data: {  id: dsId, dialogStatus:"Delete" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Delete dialog was closed',result);
      if(result){
        //this.config = result;
        //this.bodyData.id=result.toString();
        //console.log('The dialog was closed and this.bodyData:',this.bodyData);
        this.userService.deleteUser('test1',result.toString());
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
  //constructor() {}
  constructor(public dialogRef: MatDialogRef<CreateUserDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  

  onNoClick(): void {
    this.dialogRef.close();
  }
}
