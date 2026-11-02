def banditScan(String path = '.', String venv = '.venv') {
    withEnv([
        "SCAN_PATH=${path}",
        "VENV=${venv}"
    ]) {
        try {
            sh '''#!/usr/bin/env bash
            set -o pipefail

            "$VENV/bin/bandit" \
                -r "$SCAN_PATH" \
                2>&1 | tee bandit-report.txt
            '''
        } finally {
            archiveArtifacts(
                artifacts:'bandit-report.txt',
                allowEmptyArchive:true,
                fingerprint:true
            )
        }
    }
}

def semgrepScan(String venv = '.venv') {
    withEnv([
        "VENV=${venv}"
    ]) {
        try {
            sh '''#!/usr/bin/env bash
            set -o pipefail

            "$VENV/bin/semgrep" scan \
                --config=p/python \
                --metrics=off \
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
}
