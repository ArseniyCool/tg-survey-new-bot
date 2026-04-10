import http from 'k6/http';
import { check, sleep } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:800';
const webhookSecret = __ENV.WEBHOOK_SECRET || '';
const mode = (__ENV.MODE || 'smoke').toLowerCase();
const step = (__ENV.STEP || 'start').toLowerCase();
const sleepSeconds = Number(__ENV.SLEEP || '0.1');
const summaryPrefix = __ENV.SUMMARY_PREFIX || `k6-${mode}-${step}`;

const scenariosByMode = {
    smoke: {
        executor: 'constant-vus',
        vus: 10,
        duration: '15s',
    },
    medium: {
        executor: 'ramping-vus',
        stages: [
            { duration: '20s', target: 50 },
            { duration: '40s', target: 50 },
            { duration: '10s', target: 0 },
        ],
    },
    burst: {
        executor: 'ramping-vus',
        stages: [
            { duration: '10s', target: 200 },
            { duration: '20s', target: 200 },
            { duration: '10s', target: 0 },
        ],
    },
};

export const options = {
    scenarios: {
        webhook_load: scenariosByMode[mode] || scenariosByMode.smoke,
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<1500'],
    },
};

function buildHeaders() {
    const headers = {
        'Content-Type': 'application/json',
    };

    if (webhookSecret) {
        headers['X-Telegram-Bot-Api-Secret-Token'] = webhookSecret;
    }

    return headers;
}

function buildMessageText(vu, iter) {
    if (step === 'phone') {
        return `+7 (900) ${String(vu).padStart(3, '0')}-${String(iter % 100).padStart(2, '0')}-${String((iter * 7) % 100).padStart(2, '0')}`;
    }

    if (step === 'project') {
        return `Load test project ${vu}-${iter}`;
    }

    if (step === 'purpose') {
        return `Load test purpose ${vu}-${iter}`;
    }

    if (step === 'check') {
        return '/check';
    }

    return '/start';
}

function postUpdate(text, iteration, checkName) {
    const headers = buildHeaders();
    const payload = JSON.stringify(buildUpdate(__VU, iteration, text));
    const response = http.post(`${baseUrl}/telegram/webhook`, payload, { headers });

    check(response, {
        [checkName]: (r) => r.status === 200,
    });

    return response;
}

function postRawUpdate(update, checkName) {
    const headers = buildHeaders();
    const payload = JSON.stringify(update);
    const response = http.post(`${baseUrl}/telegram/webhook`, payload, { headers });

    check(response, {
        [checkName]: (r) => r.status === 200,
    });

    return response;
}

function buildUpdate(vu, iter, text, chatIdOverride = null, updateIdOverride = null) {
    const unique = vu * 100000 + iter;
    const chatId = chatIdOverride ?? (700000000 + vu);
    const now = Math.floor(Date.now() / 1000);
    const updateId = updateIdOverride ?? (900000000 + unique);

    return {
        update_id: updateId,
        message: {
            message_id: unique,
            date: now,
            chat: {
                id: chatId,
                type: 'private',
                first_name: `Load${vu}`,
            },
            from: {
                id: chatId,
                is_bot: false,
                first_name: `Load${vu}`,
                language_code: 'ru',
            },
            text,
        },
    };
}

export default function () {
    if (step === 'phone') {
        postUpdate('/start', __ITER * 2, 'start status is 200');
        postUpdate(buildMessageText(__VU, __ITER), __ITER * 2 + 1, 'phone status is 200');
    } else if (step === 'full') {
        const baseIteration = __ITER * 5;
        const phone = `+7 (900) ${String(__VU).padStart(3, '0')}-${String(__ITER % 100).padStart(2, '0')}-${String((__ITER * 7) % 100).padStart(2, '0')}`;
        const projectName = `Load test project ${__VU}-${__ITER}`;
        const purpose = `Load test purpose ${__VU}-${__ITER}`;

        postUpdate('/start', baseIteration, 'start status is 200');
        postUpdate(phone, baseIteration + 1, 'phone status is 200');
        postUpdate(projectName, baseIteration + 2, 'project status is 200');
        postUpdate(purpose, baseIteration + 3, 'purpose status is 200');
        postUpdate('/check', baseIteration + 4, 'check status is 200');
    } else if (step === 'duplicate') {
        const duplicatedUpdate = buildUpdate(
            __VU,
            __ITER,
            '/start',
            880000000 + __VU,
            990000000 + __VU * 100000 + __ITER
        );

        postRawUpdate(duplicatedUpdate, 'duplicate first status is 200');
        postRawUpdate(duplicatedUpdate, 'duplicate second status is 200');
    } else {
        postUpdate(buildMessageText(__VU, __ITER), __ITER, 'status is 200');
    }

    sleep(sleepSeconds);
}

export function handleSummary(data) {
    const summary = JSON.stringify(data, null, 2);
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const basePath = `build/${summaryPrefix}-${timestamp}`;

    return {
        stdout: summary,
        [`${basePath}.json`]: summary,
    };
}
