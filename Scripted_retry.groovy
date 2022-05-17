#!/usr/bin/env groovy

node {
   def gccHome = "/usr/bin/gcc"
   stage('Preparation') {
   //pull down Mario C code from repo
      git 'github.com/linuxacademy/content-pipelines-cje-labs.git'
   }
   try{
   stage('Build') {
      //if system is Unix compile C source code
      if (isUnix()) {
         sh "'${gccHome}' --std=c99 -o mario lab1_lab2/mario.c"
      } else {
         echo "Not a Unix system, build not possible"
      }
   }
   stage('Results') {
      archive 'mario'
   }
   } catch(Error) {
       if (currentBuild.result = 'UNSTABLE') {
       Script {
           timeout(time: 5, unit: 'Minutes') {
                retry(3) {
                    ${env.JOB_NAME}.run
                }
            }
       }
   } else {
       echo "Build is Stable"
   }
}
