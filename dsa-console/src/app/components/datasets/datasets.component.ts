import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FormControl, Validators, FormBuilder, FormGroup} from '@angular/forms';
import {DatasetsService} from '../../services/datasets.service';

import { DataSet, Configuration, BodyDataDataset} from '../../models/profile';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-datasets',
  templateUrl: './datasets.component.html',
  styleUrls: ['./datasets.component.css'],
  providers: [BodyDataDataset]
})
export class DatasetsComponent implements OnInit {
  datasets: DataSet[];
  configuration: Configuration;
  displayedColumns: any;
  dataSource: any;
  dialogStatus = '';
  domain: string;
  //heroForm: FormGroup;
  
  @ViewChild(MatSort) sort: MatSort;

  constructor(private datasetService: DatasetsService, private dialog: MatDialog, private bodydata: BodyDataDataset, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.domain = params['domain'];
      this.datasetService.getDataSets(params['domain']).then(ds => {
        this.datasets = ds;
        this.displayedColumns = ['id', 'configuration', 'modification'];
        this.dataSource = new MatTableDataSource<DataSet>(ds);
      });
    });
    //for from validation
    /*this.heroForm = new FormGroup({
      'name': new FormControl(this.bodydata.id, [
        Validators.required,
        Validators.minLength(4)
      ])
    });
    */
  }



  /**
   * Create New DataSet
   */
  openDialog4CreateDS() {
    //var bodydata: BodyData;
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id:'', dialogStatus:"TitleCreate" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed',result);
      if(result){
        //this.config = result;
        this.bodydata.domain=this.domain;
        this.bodydata.dataset=result.toString();
        this.bodydata.configurationProperties={indexFormat:"string",archivePolicy:'string',clients:['string'],dataMapping:{} };
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.datasetService.setDataset(this.domain,this.bodydata);
        //console.log('globalData in session:',sessionStorage.getItem('currentDomain'));
      }
    });
  }

  /**
   * Edit A DataSet
   */
  openDialog4EditDS(dsId: string, ds: string) {
    console.log('get dsid:', dsId);
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id: dsId, dataset: ds, dialogStatus:"TitleEdit" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Edit dialog was closed',result);
      if(result){
        //this.config = result;
        this.bodydata.domain=this.domain;
        this.bodydata.dataset=result.toString();
        this.bodydata.configurationProperties={indexFormat:"string",archivePolicy:'string',clients:['string'],dataMapping:{} };
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.datasetService.editDataset(this.domain,result.toString(),this.bodydata);
      }
      
    });
  }

  /**
   * Delete A DataSet
   */
  openDialog4DeleteDS(dsId: string, ds: string){
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id: dsId, dataset: ds, dialogStatus:"TitleDelete" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Delete dialog was closed',result);
      if(result){
        //this.config = result;
        //this.bodydata.id=result.toString();
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.datasetService.deleteDataset(this.domain,result.toString());
        
      }
      
    });
  }
  
}

/**
 * Component for Dialog
 */
@Component({
  selector : 'create-dataset-dialog',
  templateUrl : 'create-dataset-dialog.html',
  styleUrls: ['./datasets.component.css']
})
export class CreateDatasetDialogComponent {
  //constructor() {}
  constructor(public dialogRef: MatDialogRef<CreateDatasetDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  email = new FormControl('', [Validators.required, Validators.email]);
  
  getErrorMessage() {
    return this.email.hasError('required') ? 'You must enter a value' :
        this.email.hasError('email') ? 'Not a valid email' :
            '';
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
  submitted = false;
  
  onSubmit() { this.submitted = true; }
}
