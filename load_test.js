const http = require('http');

const API_URL = 'http://localhost:8080/api/query/plan';

const sendQuery = (sql) => {
    return new Promise((resolve, reject) => {
        const data = JSON.stringify({ query: sql });
        
        const startTime = process.hrtime();
        
        const req = http.request(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': data.length
            }
        }, (res) => {
            let body = '';
            res.on('data', chunk => body += chunk);
            res.on('end', () => {
                const diff = process.hrtime(startTime);
                const timeMs = (diff[0] * 1000) + (diff[1] / 1000000);
                resolve({
                    status: res.statusCode,
                    timeMs: timeMs,
                    data: JSON.parse(body)
                });
            });
        });
        
        req.on('error', (e) => reject(e));
        req.write(data);
        req.end();
    });
};

async function runTest() {
    console.log("=======================================");
    console.log("🚀 STARTING COMPREHENSIVE LOAD TEST");
    console.log("=======================================\n");

    try {
        await sendQuery("SELECT 1");
    } catch (e) {
        console.error("❌ ERROR: Backend is not running on http://localhost:8080");
        process.exit(1);
    }

    // --- PART 1: NORMAL QUERIES ---
    console.log("=======================================");
    console.log("PART 1: NORMAL QUERIES (Low Parsing Cost)");
    console.log("=======================================\n");

    console.log("▶ SCENARIO 1A: 100 Unique Normal Queries (Cache Misses)");
    let totalNormalUnique = 0;
    for (let i = 1; i <= 100; i++) {
        const result = await sendQuery(`SELECT * FROM table_${i} WHERE id = 1`);
        totalNormalUnique += result.timeMs;
    }
    const avgNormalUnique = (totalNormalUnique / 100).toFixed(2);
    console.log(`⏱ Average Latency: ${avgNormalUnique} ms\n`);

    console.log("▶ SCENARIO 1B: 100 Identical Normal Queries (Cache Hits)");
    await sendQuery("SELECT * FROM RealUsers WHERE age = 9999"); // Warmup cache miss
    let totalNormalHit = 0;
    for (let i = 1; i <= 100; i++) {
        const result = await sendQuery(`SELECT * FROM RealUsers WHERE age = ${i}`);
        totalNormalHit += result.timeMs;
    }
    const avgNormalHit = (totalNormalHit / 100).toFixed(2);
    console.log(`⏱ Average Latency: ${avgNormalHit} ms\n`);


    // --- PART 2: MASSIVE QUERIES ---
    console.log("=======================================");
    console.log("PART 2: COMPLEX QUERIES (High Parsing Cost)");
    console.log("=======================================\n");

    console.log("▶ SCENARIO 2A: 100 Unique Massive Queries (Cache Misses)");
    let totalMassiveUnique = 0;
    for (let i = 1; i <= 100; i++) {
        let inClause = Array.from({length: 80}, (_, idx) => idx + i).join(', ');
        const result = await sendQuery(`SELECT p.ProductName, c.CategoryName FROM Products_${i} p JOIN Categories_${i} c ON p.CategoryID = c.CategoryID WHERE p.ProductID IN (${inClause}) ORDER BY p.UnitPrice DESC`);
        totalMassiveUnique += result.timeMs;
    }
    const avgMassiveUnique = (totalMassiveUnique / 100).toFixed(2);
    console.log(`⏱ Average Latency: ${avgMassiveUnique} ms\n`);

    console.log("▶ SCENARIO 2B: 100 Identical Massive Queries (Cache Hits)");
    let initialIn = Array.from({length: 80}, (_, idx) => idx + 9999).join(', ');
    await sendQuery(`SELECT p.ProductName, c.CategoryName FROM SuperProducts p JOIN SuperCategories c ON p.CategoryID = c.CategoryID WHERE p.ProductID IN (${initialIn}) ORDER BY p.UnitPrice DESC`); // Warmup
    let totalMassiveHit = 0;
    for (let i = 1; i <= 100; i++) {
        let inClause = Array.from({length: 80}, (_, idx) => idx + i).join(', ');
        const result = await sendQuery(`SELECT p.ProductName, c.CategoryName FROM SuperProducts p JOIN SuperCategories c ON p.CategoryID = c.CategoryID WHERE p.ProductID IN (${inClause}) ORDER BY p.UnitPrice DESC`);
        totalMassiveHit += result.timeMs;
    }
    const avgMassiveHit = (totalMassiveHit / 100).toFixed(2);
    console.log(`⏱ Average Latency: ${avgMassiveHit} ms\n`);

    // --- SUMMARY ---
    console.log("=======================================");
    console.log("📊 PERFORMANCE IMPROVEMENT SUMMARY");
    console.log("=======================================");
    
    console.log("\n[NORMAL QUERIES]");
    console.log(`Miss (Parsing): ~${avgNormalUnique} ms  |  Hit (Cache): ~${avgNormalHit} ms`);
    console.log(`Speedup: ${((1 - (avgNormalHit / avgNormalUnique)) * 100).toFixed(2)}% (${(avgNormalUnique / avgNormalHit).toFixed(1)}x Faster)`);

    console.log("\n[COMPLEX QUERIES]");
    console.log(`Miss (Parsing): ~${avgMassiveUnique} ms  |  Hit (Cache): ~${avgMassiveHit} ms`);
    console.log(`Speedup: ${((1 - (avgMassiveHit / avgMassiveUnique)) * 100).toFixed(2)}% (${(avgMassiveUnique / avgMassiveHit).toFixed(1)}x Faster)`);
    
    console.log("=======================================");
}

runTest();
