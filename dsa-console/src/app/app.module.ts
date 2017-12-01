import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { HttpModule } from '@angular/http';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { CustomMaterialModule } from './custom-material.modul';

import { AppComponent } from './app.component';
import { AuthGuard, AppRoutingModule } from './app-routing.module';

import { LoginComponent }      from './components/login/login.component';
import { HeaderComponent, ErrorDialogComponent } from './components/header/header.component';
import { DatasetsComponent } from './components/datasets/datasets.component';
import { ManagersComponent } from './components/managers/managers.component';
import { UsersComponent } from './components/users/users.component';

import { Config } from './services/config.service';
import { LoginService } from './services/login.service';
import { DataService, requestOptionsProvider } from './services/data.service';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HeaderComponent,
    ErrorDialogComponent,
    DatasetsComponent,
    ManagersComponent,
    UsersComponent
  ],
  entryComponents: [
    ErrorDialogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CustomMaterialModule,
    HttpModule
  ],
  providers: [requestOptionsProvider,
    Config,
    DataService,
    LoginService,
    AuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }
