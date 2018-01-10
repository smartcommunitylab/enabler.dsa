// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  amUrl: 'https://api-test.smartcommunitylab.it/',
  aacUrl: 'https://am-test.smartcommunitylab.it/aac/',
  aacClientId: 'bf53cec4-e2a3-4033-a029-d128eee81951',
  redirectUrl: 'http://localhost:4200/',
  scope: 'profile.basicprofile.me,user.roles.me',
  locUrl: 'http://localhost:6030/dsa-engine/',
};
