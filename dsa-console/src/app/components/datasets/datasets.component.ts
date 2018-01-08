import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import {DatasetsService} from '../../services/datasets.service';

import { DataSet, Configuration, BodyDataDataset} from '../../models/profile';
import {ActivatedRoute} from '@angular/router';
//import { FormControl, FormGroup, Validators } from '@angular/forms';

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
      data: {  id: this.bodydata.id, dialogStatus:"TitleCreate" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed',result);
      if(result){
        //this.config = result;
        this.bodydata.id=result.toString();
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.datasetService.setDataset('test1',this.bodydata);
        //console.log('globalData in session:',sessionStorage.getItem('currentDomain'));
      }
    });
  }

  /**
   * Edit A DataSet
   */
  openDialog4EditDS(dsId: string) {
    console.log('get dsid:', dsId);
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id: dsId, dialogStatus:"TitleEdit" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Edit dialog was closed',result);
      if(result){
        //this.config = result;
        this.bodydata.id=result.toString();
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.datasetService.editDataset('test1',this.bodydata.id,this.bodydata);
        
      }
      
    });
  }

  /**
   * Delete A DataSet
   */
  openDialog4DeleteDS(dsId: string){
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id: dsId, dialogStatus:"TitleDelete" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Delete dialog was closed',result);
      if(result){
        //this.config = result;
        //this.bodydata.id=result.toString();
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.datasetService.deleteDataset('test1',result.toString());
        
      }
      
    });
  }
  
}


@Component({
  selector : 'create-dataset-dialog',
  templateUrl : 'create-dataset-dialog.html',
  //styleUrls: ['./datasets.component.css']
})
export class CreateDatasetDialogComponent {
  //constructor() {}
  constructor(public dialogRef: MatDialogRef<CreateDatasetDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  

  onNoClick(): void {
    this.dialogRef.close();
  }
  submitted = false;
  
  onSubmit() { this.submitted = true; }
}
