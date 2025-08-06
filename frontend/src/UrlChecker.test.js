import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import UrlChecker from './UrlChecker';

jest.mock('axios');

describe('UrlChecker', () => {
  test('renders the component', () => {
    render(<UrlChecker />);
    expect(screen.getByText('URL Status Checker')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter one URL per line')).toBeInTheDocument();
    expect(screen.getByText('Check URLs')).toBeInTheDocument();
  });

  test('handles URL input', () => {
    render(<UrlChecker />);
    const textarea = screen.getByPlaceholderText('Enter one URL per line');
    fireEvent.change(textarea, { target: { value: 'https://google.com' } });
    expect(textarea.value).toBe('https://google.com');
  });

  test('fetches and displays URL statuses', async () => {
    const mockData = [
      { url: 'https://google.com', status_code: 200, status: 'OK' },
      { url: 'https://github.com', status_code: 200, status: 'OK' },
    ];
    axios.post.mockResolvedValue({ data: mockData });

    render(<UrlChecker />);

    const textarea = screen.getByPlaceholderText('Enter one URL per line');
    fireEvent.change(textarea, { target: { value: 'https://google.com\nhttps://github.com' } });

    const button = screen.getByText('Check URLs');
    fireEvent.click(button);

    expect(screen.getByText('Checking...')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('https://google.com')).toBeInTheDocument();
      expect(screen.getByText('https://github.com')).toBeInTheDocument();
      expect(screen.getAllByText('200')).toHaveLength(2);
      expect(screen.getAllByText('OK')).toHaveLength(2);
    });

    expect(screen.getByText('Check URLs')).toBeInTheDocument();
  });

  test('handles API error', async () => {
    axios.post.mockRejectedValue(new Error('API Error'));

    render(<UrlChecker />);

    const textarea = screen.getByPlaceholderText('Enter one URL per line');
    fireEvent.change(textarea, { target: { value: 'https://google.com' } });

    const button = screen.getByText('Check URLs');
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText('Check URLs')).toBeInTheDocument();
    });

    // We can't easily test the console.error, but we can ensure the app doesn't crash
    // and the loading state is reset.
  });
});
