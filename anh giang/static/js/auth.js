async function fetchData() {
    const token = localStorage.getItem('token');

    const response = await fetch('/api/some-protected-resource', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.ok) {
        const data = await response.json();
        // Xử lý dữ liệu
        console.log(data);
    } else {
        alert('Không có quyền truy cập hoặc token không hợp lệ.');
        // Chuyển hướng về trang đăng nhập nếu token không hợp lệ
        window.location.href = '/login'; // Thay đổi đường dẫn nếu cần
    }
}

window.onload = function() {
    const token = localStorage.getItem('token');

    if (!token) {
        // Nếu không có token, chuyển hướng về trang đăng nhập
        window.location.href = '/login'; // Thay đổi đường dẫn nếu cần
    } else {
        // Nếu có token, bạn có thể xác thực nó với server
        fetchData(); // Gọi hàm để lấy dữ liệu nếu cần
    }
};
