import { Component, OnInit } from '@angular/core';
<<<<<<< HEAD
=======
import {LoginService} from '../../services/login.service';
>>>>>>> e081abef02326bb362233c8283b6aee8c72bf034

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

<<<<<<< HEAD
  constructor() { }

  ngOnInit() {
=======
  constructor(private loginService: LoginService) { }

  ngOnInit(): void {
    this.loginService.login();
>>>>>>> e081abef02326bb362233c8283b6aee8c72bf034
  }

}
