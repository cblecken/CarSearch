#!/bin/bash

s3bucket=${1}
if [ -z "${s3bucket}" ]; then
    echo "provide the s3 bucket"
    exit 1
fi
s3key=${2}
if [ -z "${s3key}" ]; then
	echo "provide the s3 key (filename)"
    exit 1
fi
appname=${3}
if [ -z "${appname}" ]; then
	echo "provide the existing application name"
    exit 1
fi
version=${4}
if [ -z "${version}" ]; then
	echo "provide the new version label"
    exit 1
fi
# copy the exec jar to s3
aws s3 cp ./build/libs/safe2-0.0.1-SNAPSHOT.jar s3://${s3bucket}/{s3key}

# update the existing application in beanstalk
aws elasticbeanstalk create-application-version --application-name ${appname} --version-label ${version} --source-bundle S3Bucket=${s3bucket},S3Key=${s3key}



