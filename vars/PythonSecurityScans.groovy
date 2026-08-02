def semgrepScan(String venv = '.venv') {
    withEnv([
        "VENV=${venv}"
    ]) {
        try {
            sh '''
            #!/usr/bin/env bash
            set -o pipefail

            "$VENV/bin/semgrep" scan \
                --config=p/python \
                --metrics=off \
                --error \
                2>&1 | tee semgrep-report.txt
            '''
        } finally {
            archiveArtifacts(
                artifacts: 'semgrep-report.txt',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }
    }
}
