import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatButtonModule, MatCheckboxModule, MatSidenavModule, MatToolbarModule, MatIconModule, MatMenuModule, MatTableModule, MatSelectModule, MatDialogModule } from '@angular/material';

@NgModule({
  imports: [MatButtonModule, MatCheckboxModule, MatSidenavModule, MatToolbarModule, MatIconModule, MatMenuModule, MatTableModule, MatSelectModule, MatDialogModule],
  exports: [MatButtonModule, MatCheckboxModule, MatSidenavModule, MatToolbarModule, MatIconModule, MatMenuModule, MatTableModule, MatSelectModule, MatDialogModule],
  declarations: []
})
export class CustomMaterialModule { }
