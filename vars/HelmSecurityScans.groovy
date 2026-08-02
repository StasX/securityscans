def checkovScan(
    String path,
) {
    withEnv([
        "SCAN_PATH=${path}",
        "PATH_FLAG=${pathFlag}",
        "FRAMEWORK=${framework}",
        "VENV=${venv}"
    ]) {
        try {
            sh '''
            #!/usr/bin/env bash
            set -o pipefail

            ".venv/bin/python" -m checkov.main \
                "-d" "$SCAN_PATH" \
                --framework helm \
                2>&1 | tee checkov-report.txt
            '''
        } finally {
            archiveArtifacts(
                artifacts:'checkov-report.txt',
                allowEmptyArchive:true,
                fingerprint:true
            )
        }
    }
}

def kubeScoreScan(
    String path,
    String file
) {
    withEnv([
        "SCAN_PATH=${path}",
        "SCAN_FILE=${file}"
    ]) {
        try {
            sh '''
            #!/usr/bin/env bash
            set -o pipefail

            docker run --rm \
                -v "$SCAN_PATH:/work:ro" \
                zegl/kube-score:latest \
                score "/work/$SCAN_FILE" \
                2>&1 | tee kube-score-report.txt
            '''
        } finally {
            archiveArtifacts(
                artifacts:'kube-score-report.txt',
                allowEmptyArchive:true,
                fingerprint:true
            )
        }
    }
}

def semgrepScan() {
    try {
        sh '''
        #!/usr/bin/env bash
        set -o pipefail

        ".venv/bin/semgrep" scan \
            --config=p/kubernetes \
            --metrics=on \
            --error \
            2>&1 | tee semgrep-report.txt
        '''
    } finally {
        archiveArtifacts(
            artifacts:'semgrep-report.txt',
            allowEmptyArchive:true,
            fingerprint:true
        )
    }
}
