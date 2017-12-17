import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {DatasetsService} from '../../services/datasets.service';

import { DataSet, Configuration, BodyData} from '../../models/profile';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-datasets',
  templateUrl: './datasets.component.html',
  styleUrls: ['./datasets.component.css'],
  providers:[BodyData]
})
export class DatasetsComponent implements OnInit {
  datasets: DataSet[];
  configuration: Configuration;
  displayedColumns: any;
  dataSource: any;
  dialogStatus: string="";
  domain:any;
  sub:any;

  constructor(private datasetService: DatasetsService, private dialog: MatDialog,private bodydata: BodyData,private route: ActivatedRoute) {
    //var bodydata: BodyData;
  }

  ngOnInit() {
    this.sub=this.route.params.subscribe(params=>{
      this.domain=params['domain'];
      this.datasetService.getDataSets(params['domain']).then(ds => {
        this.datasets = ds;
        this.displayedColumns = ['id', 'configuration','modification'];
        this.dataSource = new MatTableDataSource<DataSet>(ds);
      });
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
  
  
  /**
   * Create New DataSet
   */
  openDialog4CreateDS() {
    //var bodydata: BodyData;
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
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
        this.datasetService.setDataset('test1',this.bodydata);
        //console.log('globalData in session:',sessionStorage.getItem('currentDomain'));
      }
      
    });
  }

  /**
   * Edit A DataSet
   */
  openDialog4EditDS(dsId: string){
    console.log("get dsid:",dsId);
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
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
        this.datasetService.editDataset('test1',this.bodydata.id,this.bodydata);
        
      }
      
    });
  }

  /**
   * Delete A DataSet
   */
  openDialog4DeleteDS(dsId: string){
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
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
        this.datasetService.deleteDataset('test1',result.toString());
        
      }
      
    });
  }
}


@Component({
  selector : 'create-dataset-dialog',
  templateUrl : 'create-dataset-dialog.html',
  styleUrls: ['./datasets.component.css']
})
export class CreateDatasetDialogComponent {
  //constructor() {}
  constructor(public dialogRef: MatDialogRef<CreateDatasetDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  

  onNoClick(): void {
    this.dialogRef.close();
  }
}
