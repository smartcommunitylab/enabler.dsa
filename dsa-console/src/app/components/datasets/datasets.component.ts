import { Component, OnInit, ViewChild, Inject } from '@angular/core';
import {MatTableDataSource, MatSort, MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FormControl, Validators, FormBuilder, FormGroup} from '@angular/forms';
import {DatasetsService} from '../../services/datasets.service';

import { DataSet, BodyDataDataset, ConfigurationProperties} from '../../models/profile';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-datasets',
  templateUrl: './datasets.component.html',
  styleUrls: ['./datasets.component.css'],
  providers: [BodyDataDataset]
})
export class DatasetsComponent implements OnInit {
  datasets: DataSet[];
  displayedColumns: any;
  dataSource: any;
  dialogStatus = '';
  domain: string;
  arcPoliSelected: string;
  arcPoli: string;
  arcPoliLimit: any;
  //heroForm: FormGroup;
  
  @ViewChild(MatSort) sort: MatSort;

  constructor(private datasetService: DatasetsService, private dialog: MatDialog, private bodydata: BodyDataDataset, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.domain = params['domain'];
      this.datasetService.getDataSets(params['domain']).then(ds => {
        this.datasets = ds;
        this.displayedColumns = ['id', 'inFormat', 'arPolicy', 'clients', 'modification'];
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
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id:'',dataset:'', dialogStatus:"TitleCreate" }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result){
        console.log('The dialog was closed, dataset:',result.ds);
        this.bodydata.domain=this.domain;
        this.bodydata.dataset=result.ds.toString();
        this.bodydata.configurationProperties={indexFormat:"none",archivePolicy:"none",clients:['string'],dataMapping:{} };
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        this.datasetService.setDataset(this.domain,this.bodydata);
        //console.log('globalData in session:',sessionStorage.getItem('currentDomain'));
        //for reload the table
        setTimeout(()=>{  this.ngOnInit();},1000);
      }
    });
  }

  /**
   * Edit A DataSet
   */
  openDialog4EditDS(dsId: string, ds: string, configPro:ConfigurationProperties) {
    console.log('ConfigurationProperties(in openDialog4EditDS):', configPro);
    if(configPro.archivePolicy == 'none'){
      this.arcPoli="none";
      this.arcPoliLimit=0;
    }else{
      this.arcPoli=configPro.archivePolicy.substr(-1);
      this.arcPoliLimit=configPro.archivePolicy.split(this.arcPoli)[0];
    }
    let dialogRef = this.dialog.open(CreateDatasetDialogComponent,{
      //height: '300px',
      width: '300px',
      data: {  id: dsId, dataset: ds, configPro: configPro, indexSelected:configPro.indexFormat, arcPolicySelected:this.arcPoli, archivePolicyLimit:this.arcPoliLimit, dialogStatus:"TitleEdit" }
    });
    dialogRef.afterClosed().subscribe(result => {
      //console.log('The Edit dialog was closed, result',result.configPro);
      if(result){
        this.arcPoliSelected=result.arcPolicyLimit+result.arcPolicy;
        this.bodydata.domain=this.domain;
        this.bodydata.dataset=result.ds;
        this.bodydata.configurationProperties={indexFormat:result.inFormat, archivePolicy:this.arcPoliSelected, clients:['clients'],dataMapping:{} };
        //this.bodydata.configurationProperties=result.configPro;
        this.datasetService.editDataset(this.domain,result.id,this.bodydata);
        console.log('The dialog was closed and this.bodydata:',this.bodydata);
        //for reload the table
        setTimeout(()=>{  this.ngOnInit();},1000);
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
      data: {  id: dsId, dataset: ds, dsDelete: ds, dialogStatus:"TitleDelete" }
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('The Delete dialog was closed',result);
      if(result){
        this.datasetService.deleteDataset(this.domain,result.id);
        //for reload the table
        setTimeout(()=>{  this.ngOnInit();},1000);
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
  selected="option1";

  constructor(public dialogRef: MatDialogRef<CreateDatasetDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  dsControl = new FormControl('', [Validators.required]);
  dsEdIndexCon = new FormControl('', [Validators.required]);
  arcPoliLimitControl = new FormControl('', [Validators.min(0)]);
  getErrorMessage4ds() {
    return this.dsControl.hasError('required') ? 'You must enter a value' :
        //this.dataset.hasError('email') ? 'Not a valid email' :
            '';
  }
  getErrorMessage4arcPolLimit(){
    return this.arcPoliLimitControl.hasError('required') ? 'You must enter a value!' :
        this.arcPoliLimitControl.hasError('min') ? 'You must enter positive number!' :
            '';
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
  submitted = false;
  
  onSubmit() { this.submitted = true; }
}
