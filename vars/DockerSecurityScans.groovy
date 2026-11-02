def trivyScanImage(
    String image,
    String tag = 'latest'
) {
    withEnv([
        "IMAGE=${image}:${tag}"
    ]) {
        try {
            sh '''#!/usr/bin/env bash
            set -o pipefail

            docker run --rm \
                -v /var/run/docker.sock:/var/run/docker.sock \
                aquasec/trivy:latest image \
                "$IMAGE" \
                --severity HIGH,CRITICAL \
                --exit-code 1 \
                2>&1 | tee trivy-image.log
            '''
        } finally {
            archiveArtifacts(
                artifacts:'trivy-image.log',
                allowEmptyArchive:true,
                fingerprint:true
            )
        }
    }
}

def trivyScanFile(
    String path,
    String file
) {
    withEnv([
        "SCAN_PATH=${path}",
        "SCAN_FILE=${file}"
    ]) {
        try {
            sh '''#!/usr/bin/env bash
            set -o pipefail

            docker run --rm \
                -v "$SCAN_PATH:/work" \
                -w /work \
                aquasec/trivy:latest config \
                "$SCAN_FILE" \
                --severity HIGH,CRITICAL \
                --exit-code 1 \
                2>&1 | tee trivy-config.log
            '''
        } finally {
            archiveArtifacts(
                artifacts:'trivy-config.log',
                allowEmptyArchive:true,
                fingerprint:true
            )
        }
    }
}

def checkovScan(
    String path
) {
    withEnv([
        "SCAN_PATH=${path}"
    ]) {
        try {
            sh '''#!/usr/bin/env bash
            set -o pipefail

            .venv/bin/python -m checkov.main \
                -f "$SCAN_PATH" \
                --framework dockerfile \
                2>&1 | tee checkov.log
            '''
        } finally {
            archiveArtifacts(
                artifacts:'checkov.log',
                allowEmptyArchive:true,
                fingerprint:true
            )
        }
    }
}

def semgrepScan() {
    try {
        sh '''#!/usr/bin/env bash
        set -o pipefail

        .venv/bin/semgrep scan \
            --config p/dockerfile \
            --metrics on \
            --error \
            2>&1 | tee semgrep.log
        '''
    } finally {
        archiveArtifacts(
            artifacts:'semgrep.log',
            allowEmptyArchive:true,
            fingerprint:true
        )
    }
}
