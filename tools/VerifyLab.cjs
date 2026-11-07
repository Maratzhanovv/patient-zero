const fs=require('fs'),vm=require('vm');
const html=fs.readFileSync('output/outbreak.html','utf8');
const script=html.split('<script>')[1].split('/* ---------- hero, board, progress ---------- */')[0];
const result=vm.runInNewContext(script+';({lineOK,coverOK,truth,hits:JSON.stringify(L.hits)===JSON.stringify(D.hits)})');
if(!result.lineOK.every(Boolean)||!result.coverOK||!result.hits) throw new Error(JSON.stringify(result));
console.log('Unmodified lab calculations: all 9 values agree; chosen people cover every edge; hit positions agree.');
console.log(JSON.stringify(result.truth));
