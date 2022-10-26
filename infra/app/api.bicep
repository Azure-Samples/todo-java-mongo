param name string
param location string = resourceGroup().location
param tags object = {}

param allowedOrigins array = []
param appCommandLine string = ''
param applicationInsightsName string = ''
param appServicePlanId string
param appSettings object = {}
param keyVaultName string
param serviceName string = 'api'

module api '../core/host/appservice.bicep' = {
  name: '${name}-app-module'
  params: {
    name: name
    location: location
    tags: union(tags, { 'azd-service-name': serviceName })
    allowedOrigins: allowedOrigins
    appCommandLine: appCommandLine
    applicationInsightsName: applicationInsightsName
    appServicePlanId: appServicePlanId
    appSettings: appSettings
    keyVaultName: keyVaultName
    runtimeName: 'java'
    runtimeVersion: '17-java17'
    scmDoBuildDuringDeployment: true
  }
}

output API_IDENTITY_PRINCIPAL_ID string = api.outputs.identityPrincipalId
output API_NAME string = api.outputs.name
output API_URI string = api.outputs.uri
