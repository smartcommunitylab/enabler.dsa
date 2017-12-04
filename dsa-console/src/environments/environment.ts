// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  amUrl: 'https://api-dev.smartcommunitylab.it/',
  aacUrl: 'https://am-dev.smartcommunitylab.it/aac/',
  aacClientId: '203d85ec-009c-4c1b-bc34-f9bed347f22d',
  redirectUrl: 'http://localhost:4200/',
  scope: 'profile.basicprofile.me,user.roles.me'
};
