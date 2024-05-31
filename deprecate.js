const fs = require('fs');
const path = require('path');

const files = fs.readdirSync('./', { recursive: true }).filter(file => file.endsWith('.java'));
for (const file of files) {
    /* fs.readFile(file, 'utf8', (err, data) => {
        if (err) {
            console.error('Error reading file:', err);
            return;
        }
    
        const methodRegex = /(\s*public\s+|private\s+|protected\s+|static\s+|void\s+|int\s+|boolean\s+|String\s+|float\s+|double\s+|char\s+|byte\s+|short\s+|long\s+|interface\s+|enum\s+|abstract\s+|class\s+|native\s+|synchronized\s+|transient\s+|volatile\s+|final\s+|strictfp\s+)+(\w+\s*\([^)]*\)\s*{)/g;
    
        const insertDeprecatedComment = (match) => {
            return `\n    /**\n     * @deprecated \n     n    ${match}`;
        };
    
        const updatedData = data.replace(methodRegex, insertDeprecatedComment);
    
        fs.writeFile(file, updatedData, 'utf8', (err) => {
            if (err) {
                console.error('Error writing file:', err);
                return;
            }
            console.log('File updated with @deprecated comments.');
        });
    });*/

    fs.readFile(file, 'utf8', (err, data) => {
        if (err) {
            console.error('Error reading file:', err);
            return;
        }
    
        const variableRegex = /\b(?:public|private|protected|static|final|volatile|transient|synchronized|abstract|native|strictfp)?\s*(?:\w+\s+)+(\w+)\s*=/g;
    
        let count = 1;
        const updatedData = data.replace(variableRegex, (match, p1) => {
            return match.replace(p1, `$$${count++}`);
        });
    
        fs.writeFile(file, updatedData, 'utf8', (err) => {
            if (err) {
                console.error('Error writing file:', err);
                return;
            }
            console.log('Replaced.' + count);
        });
    });
}