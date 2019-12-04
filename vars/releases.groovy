#!/usr/bin/env groovy

@Grab(group='io.github.http-builder-ng', module='http-builder-ng-core', version='1.0.4')

import groovyx.net.http.*

@NonCPS
def githubClient(token) {
    return HttpBuilder.configure {
        request.uri = 'https://api.github.com'
        request.accept = ['application/vnd.github.v3+json']
        request.headers['Authorization'] = "token ${token}"
    }
}

@NonCPS
def create(token, owner, repo, releaseName) {
    def releaseInfo = githubClient(token).post {
        request.uri.path = "/repos/${owner}/${repo}/releases"
        request.contentType = ContentTypes.JSON[0]
        request.body = [
            tag_name: releaseName,
            target_commitish: "master",
            name: releaseName
        ]
    }
    return releaseInfo["id"]
}

@NonCPS
def uploadArtifact(token, owner, repo, releaseId, artifactName, fileName) {
    def f = new File(fileName)
    def uri = "https://uploads.github.com/repos/${owner}/${repo}/releases/${releaseId}/assets?name=${artifactName}"
    githubClient(token).post {
        request.uri = uri
        request.contentType = 'application/octet-stream'
        request.body = f.bytes
    }
}
