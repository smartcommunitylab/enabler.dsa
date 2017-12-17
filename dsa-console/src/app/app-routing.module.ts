import { NgModule, Injectable } from '@angular/core';
import { RouterModule, Routes, Router, CanActivate, CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { LoginComponent }       from './components/login/login.component';
import { HeaderComponent }      from './components/header/header.component';
import { DatasetsComponent }    from './components/datasets/datasets.component';
import { ManagersComponent }    from './components/managers/managers.component';
import { UsersComponent }       from './components/users/users.component';

import { LoginService }         from './services/login.service';
import { Promise } from 'q';

/**
 * Authentication guard for the console
 */
@Injectable()
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(private login: LoginService, private router: Router) {}

  /**
   * Can navigate to internal pages only if the user is authenticated
   * @param route
   * @param state
   */
  
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    console.log('AuthGuard#canActivate called');
    return this.login.checkLoginStatus().then(valid => {
      if (!valid) {
        console.log("come here for not valid");
        this.router.navigate(['/login']);
      }
      return valid;
    });
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.canActivate(route, state);
  }
}

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: HeaderComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        canActivateChild: [AuthGuard],
        children: [
          { path: 'datasets', component: DatasetsComponent, canActivate: [AuthGuard] },
          { path: 'datasets/:domain', component: DatasetsComponent, canActivate: [AuthGuard] },
          { path: 'managers', component: ManagersComponent, canActivate: [AuthGuard] },
          { path: 'users', component: UsersComponent, canActivate: [AuthGuard] },
          { path: '', redirectTo: 'datasets/', pathMatch: 'full'},
          { path: '**', redirectTo: 'datasets/', pathMatch: 'full'}
        ]
      }
    ]
  }

];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }

