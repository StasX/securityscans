def PHPStanScan() {
    try {
        sh '''
        #!/usr/bin/env bash
        set -o pipefail

        phpstan analyse \
            2>&1 | tee phpstan-report.txt
        '''
    } finally {
        archiveArtifacts(
            artifacts: 'phpstan-report.txt',
            allowEmptyArchive: true,
            fingerprint: true
        )
    }
}

def semgrepScan() {
    try {
        sh '''
        #!/usr/bin/env bash
        set -o pipefail

        "$VENV/bin/semgrep" scan \
            --config=p/php \
            --metrics=on \
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
