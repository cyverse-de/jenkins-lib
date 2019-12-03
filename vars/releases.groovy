#!/usr/bin/env groovy

@Grab('io.github.http-builder-ng:http-builder-ng-core:1.0.4')

import groovyx.net.http.HttpBuilder
import groovyx.net.http.ContentTypes

def githubClient(token) {
    return HttpBuilder.configure {
        request.uri = 'https://api.github.com'
        request.accept = ['application/vnd.github.v3+json']
        request.headers['Authorization'] = "token ${token}"
    }
}

def create(token, owner, repo, releaseName) {
    return githubClient(token).post {
        request.uri.path = "/repos/${owner}/${repo}/releases"
        request.contentType = ContentTypes.JSON[0]
        request.body = [
            tag_name: releaseName,
            target_commitish: "master",
            name: releaseName
        ]
    }['id']
}

def uploadArtifact(token, owner, repo, releaseId, artifactName, artifactContents) {
    return githubClient(token).post {
        request.uri = "https://uploads.github.com/repos/${owner}/${repo}/releases/${releaseId}/assets"
        request.contentType = 'application/octet-stream'
        request.uri.query = [name: artifactName]
        request.body = artifactContents
    }
}
